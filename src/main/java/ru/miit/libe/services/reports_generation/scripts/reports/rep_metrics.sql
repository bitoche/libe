-- "Обращаемость" (Appeal rate) — среднее число книговыдач на единицу фонда (на книгу).

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
    metric varchar,
    metric_unit varchar,
    value numeric,
    metric_desc varchar
)
;

-- удаление записей с тем же calc_id
delete from $reports_schema_value.$report_table_name_value
where calc_id = $calc_id_value
;

--основной скрипт
insert into $reports_schema_value.$report_table_name_value
select
    $calc_id_value::int as calc_id,
    '$report_date_value'::date as report_date,
    '$filtered_start_date_value' || ' - ' || '$filtered_end_date_value' as filter_range,
    metric,
    metric_unit,
    value,
    metric_desc
from (
    -- "Книгообеспеченность" (Book security) — среднее количество книг, приходящихся на одного зарегистрированного читателя.
    select 
        'Книгообеспеченность' as metric,
        'Коэффициент' as metric_unit,
        ROUND(books_count/users_count::numeric, 2) as value,
        'Cреднее количество книг, приходящихся на одного зарегистрированного читателя' as metric_desc
    from (
        WITH
        prep__books as (
            select
                count(*) as books_count
            from $extracted_schema_name_value.book
        ),
        prep__readers as (
            SELECT
                count(*) as users_count
            from $extracted_schema_name_value.user u
            where u.role <> 0 and u.role <> 1 -- только те, кто может брать книги
        )
        SELECT
            b.books_count,
            r.users_count
        from prep__books b
        join prep__readers r
        on true
    ) t1

    union ALL

    -- "Процент учащихся" (Percentage of students) - общее число пользователей деленное на число студентов
    select 
        'Процент учащихся' as metric,
        'Проценты' as metric_unit,
        ROUND(students_count/users_count::numeric*100,2) as value,
        'Общее число пользователей деленное на число студентов' as metric_desc
    from (
        WITH
        prep__students as (
            select
                count(*) as students_count
            from $extracted_schema_name_value.user u
            where u.role = 2 -- только студенты
        ),
        prep__users as (
            SELECT
                count(*) as users_count
            from $extracted_schema_name_value.user u -- все пользователи
        )
        SELECT
            s.students_count,
            u.users_count
        from prep__students s
        join prep__users u
        on true
    ) t2

    union ALL

    -- "Показатель нагрузки библиотекаря" (Librarian's workload indicator) — Число читателей / на число библиотекарей
    select 
        'Нагрузка библиотекаря' as metric,
        'Коэффициент' as metric_unit,
        ROUND(readers_count/librarians_count::numeric, 2) as value,
        'Число читателей деленное на число библиотекарей' as metric_desc
    from (
        WITH
        prep__librarians as (
            SELECT
                count(*) as librarians_count
            from $extracted_schema_name_value.user u
            where u.role = 4 -- только библиотекари
        ),
        prep__readers as (
            SELECT
                count(*) as readers_count
            from $extracted_schema_name_value.user u
            where u.role <> 0 and u.role <> 1 and u.role <> 4 -- только те, кто может брать книги, кроме библиотекарей
        )
        SELECT
            l.librarians_count,
            r.readers_count
        from prep__librarians l
        join prep__readers r
        on true
    ) t3

    -- Показатель "Читаемость" (Readability) — среднее число книг, выданных одному читателю за период.
    union all

    select
        'Читаемость' as metric,
        'Штук' as metric_unit,
        CASE WHEN
            ROUND(sum(reciepted_books_count)/count(distinct reader)::numeric, 2) is NULL
            then 0
            else ROUND(sum(reciepted_books_count)/count(distinct reader)::numeric, 2)
        end as value,
        --ROUND(sum(reciepted_books_count)/count(distinct reader)::numeric, 2) as value,
        'Среднее число книг, выданных одному читателю за период' as metric_desc
    from $reports_schema_value.rep_readability v 
    where v.calc_id = $calc_id_value
    and v.action_name = 'Reciept'

    -- Показатель "Обращаемость" (Appeal rate) — среднее число книговыдач на единицу фонда (на книгу).
    union all

    select
        'Обращаемость' as metric,
        'Раз' as metric_unit,
        case WHEN
            ROUND(sum(book_distribution_count)/count(distinct book)::numeric, 2) is NULL
            then 0
            else ROUND(sum(book_distribution_count)/count(distinct book)::numeric, 2)
        end as value,
        'Среднее число книговыдач на единицу фонда' as metric_desc
    from $reports_schema_value.rep_appeal_rate ar
    where ar.calc_id = $calc_id_value
) t
    