-- Показатель "Читаемость" (Readability) — среднее число книг, выданных одному читателю за период.

-- variables --

-- $reports_schema_value - схема, в которой будет создаваться отчет
-- $report_table_name_value - название выходной витрины
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
    action_name varchar,
    reader varchar,
    reciepted_books_count int
)
;

-- удаление записей с тем же calc_id
delete from $reports_schema_value.$report_table_name_value
where calc_id = $calc_id_value
;

-- основной скрипт
insert into $reports_schema_value.$report_table_name_value -- Фильтрация по дате, группировка по пользователям с общим итогом
SELECT * from (
WITH

prep__reciepted_borrows as (
    select * from $extracted_schema_name_value.borrow
    where fact_reciept_dttm BETWEEN '$filtered_start_date_value'::date AND '$filtered_end_date_value'::date  -- условие фильтрации по дате [5]
),
prep__returned_borrows as (
    select * from $extracted_schema_name_value.borrow
    where fact_return_dttm BETWEEN '$filtered_start_date_value'::date AND '$filtered_end_date_value'::date
),
prep__result as (
    SELECT
        borrowed_user_user_id as user,
        COUNT(*) as reciepted_books_count ,
        'Reciept' as action_name
    from prep__reciepted_borrows
    GROUP by borrowed_user_user_id

    union all

    SELECT
        borrowed_user_user_id as user,
        COUNT(*) as reciepted_books_count ,
        'Return' as action_name
    from prep__returned_borrows
    GROUP by borrowed_user_user_id
),
prep__ref_users as (
    select 
        user_id,
        email
    from $extracted_schema_name_value.user
)
select 
    $calc_id_value::int as calc_id,
    '$report_date_value'::date as report_date,
    '$filtered_start_date_value' || ' - ' || '$filtered_end_date_value' as filter_range,
    r.action_name,
    ru.email as reader,
    reciepted_books_count
from prep__result r
left join prep__ref_users ru
    on r.user = ru.user_id
) t
;