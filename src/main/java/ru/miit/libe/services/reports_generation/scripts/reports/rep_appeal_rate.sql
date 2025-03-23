-- Показатель "Обращаемость" (Appeal rate) — среднее число книговыдач на единицу фонда (на книгу).

-- $reports_schema_valuе = $reports_schema_value - название схемы под отчеты
-- $report_table_name_valuе = $report_table_name_value - название таблицы для отчета
-- $calc_id_valuе = $calc_id_value - текущий calc_id
-- $extracted_schema_name_value - схема с таблицами входных данных для отчета
-- $calc_id_value - 
-- $report_date_value - 
-- $filtered_start_date_value - 
-- $filtered_end_date_value - 


-- создание схемы под отчеты, если ее нет
create schema if not exists $reports_schema_value
;

-- создание таблицы под отчет, если ее нет
create table if not exists $reports_schema_value.$report_table_name_value (
    calc_id int,
    report_date date,
    filter_range varchar,
    book varchar,
    book_distribution_count int
)
;

-- удаление записей с тем же calc_id
delete from $reports_schema_value.$report_table_name_value
where calc_id = $calc_id_value
;

--основной скрипт
insert into $reports_schema_value.$report_table_name_value
select * from (
    with 

    prep__reciepted_borrows as (
        select * from $extracted_schema_name_value.borrow
        where fact_reciept_dttm BETWEEN '$filtered_start_date_value'::date AND '$filtered_end_date_value'::date  -- условие фильтрации по дате [5]
    ),

    prep__ref_books as (
        select 
            identifier,
            book_name, 
            book_id
        from $extracted_schema_name_value.book
    ),

    prep__result as (
        SELECT
            borrowed_book_book_id as book,
            count(*) as book_distribution_count
        from prep__reciepted_borrows
        group by borrowed_book_book_id
    )

    select 
        $calc_id_value::int as calc_id,
        '$report_date_value'::date as report_date,
        '$filtered_start_date_value' || ' - ' || '$filtered_end_date_value' as filter_range,
        rb.identifier as book,
        book_distribution_count
    from prep__result r
    left join prep__ref_books rb
        on r.book = rb.book_id
) t
    