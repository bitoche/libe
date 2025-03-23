-- $reports_schema_valuе = $reports_schema_value - название схемы под отчеты
-- $report_table_name_valuе = $report_table_name_value - название таблицы для отчета
-- $calc_id_valuе = $calc_id_value - текущий calc_id
-- $#columns_to_be_in_rep_tablе - перечисление столбцов выходной таблицы str-SQL
-- $#select_to_rep_table_scripт - основной скрипт селекта нужной инфы


-- создание схемы под отчеты, если ее нет
create schema if not exists $reports_schema_value
;

-- создание таблицы под отчет, если ее нет
create table if not exists $reports_schema_value.$report_table_name_value (
    $#columns_to_be_in_rep_table
)
;

-- удаление записей с тем же calc_id
delete from $reports_schema_value.$report_table_name_value
where calc_id = $calc_id_value
;

--основной скрипт
insert into $reports_schema_value.$report_table_name_value
$#select_to_rep_table_script
;