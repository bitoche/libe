-- Показатель "Читаемость" (Readability) — среднее число книг, выданных одному читателю за период.

-- variables --

-- $reports_schema_value - схема, в которой будет создаваться отчет
-- $extracted_schema_name_value - схема с таблицами входных данных для отчета
-- $calc_id_value - 
-- $report_date_value - 
-- $filtered_start_date_value - 
-- $filtered_end_date_value - 

-- создание схемы под отчеты, если ее нет
create schema if not exists $reports_schema_value
;

-- создание таблицы под отчет, если ее нет
create table if not exists $reports_schema_value.rep_readablility (
    calc_id int,
    report_date date,
    filter_range varchar,
    reader varchar,
    reciepted_books_count int
)
;

-- удаление записей с тем же calc_id
delete from $reports_schema_value.rep_readablility
where calc_id = $calc_id_value
;

-- основной скрипт
insert into $reports_schema_value.rep_readablility -- Фильтрация по дате, группировка по пользователям с общим итогом
SELECT 
    $calc_id_value::int as calc_id,
    '$report_date_value'::date as report_date,
    '$filtered_start_date_value' || ' - ' || '$filtered_end_date_value' as filter_range,
    CASE WHEN GROUPING(borrowed_user_user_id) = 1 THEN 'all' 
        ELSE borrowed_user_user_id::TEXT 
    END AS "reader",
    COUNT(*) AS reciepted_books_count
FROM $extracted_schema_name_value.borrow
WHERE 
    fact_reciept_dttm BETWEEN '$filtered_start_date_value'::date AND '$filtered_end_date_value'::date  -- условие фильтрации по дате [5]
GROUP BY GROUPING SETS (borrowed_user_user_id, ())            -- группировка по пользователю и общий итог [4][5]
ORDER BY GROUPING(borrowed_user_user_id), borrowed_user_user_id
;