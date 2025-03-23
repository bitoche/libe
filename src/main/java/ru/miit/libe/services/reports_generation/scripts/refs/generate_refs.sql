-- variables --

-- $reports_schema_value - схема, в которой будет создаваться отчет
-- $report_table_name_value - название выходной витрины
-- $extracted_schema_name_value - схема с таблицами входных данных для отчета
-- $calc_id_value - 
-- $report_date_value - 
-- $filtered_start_date_value - 
-- $filtered_end_date_value - 

drop table if exists $reports_schema_value.ref_borrow_status;

create table $reports_schema_value.ref_borrow_status (
    status_name varchar,
    status_desc varchar,
    status_code int
)
;

insert into $reports_schema_value.ref_borrow_status values 
('ON_HANDS', 'На руках', 0),
('RETURNED','Возвращена',1),
('LOST','Утеряна, не уплачено',2),
('LOST_AND_PAID','Утеряна, уплачено',3),
('AWAITING_RECIEPT','Ожидает получения',4)
;

