from config import DB_CONFIG, watch_env_params
from pathlib import Path
from datetime import datetime
from helpers import replace_all, read_file_data, execute_query
import pandas

def start_reports(params:dict):
    print(f'started calc with params: {params}')
    
    MODULE_PATH = Path.cwd()
    SCRIPTS_PATH = MODULE_PATH / 'scripts'

    datetime_to_str_replaces = {
        '.': '',
        '-': '',
        ':': '',
        ' ': '_'
    }

    reports = [
        "rep_readability",
        "rep_appeal_rate",
        "rep_metrics" # должна быть в конце, т.к. использует предыдущие витрины
    ]
    refs = [
        "generate_refs"
    ]

    # загрузка справочников
    refs_variables = {
        '$reports_schema_value': DB_CONFIG['REPORTS_SCHEMA']
    }
    for r in refs:
        ref_params = {
            'refs_scripts_paths': SCRIPTS_PATH / 'refs' / f'{r}.sql',
        }
        raw_query = read_file_data(ref_params['refs_scripts_paths'], debug=True)
        ready_query = replace_all(raw_query, refs_variables, debug=False)
        execute_query(ready_query, debug=True)


    # выполнение отчетов
    for r in reports:
        print(f'started {r}\nenv_params: {watch_env_params()}')

        raw_data_schema = 'public'

        report_params = {
            'report_name': r,
            'report_script_path': SCRIPTS_PATH / 'reports' / f'{r}.sql',
        }
        report_variables = {
            '$calc_id_value': params['calc_id'],
            '$reports_schema_value': DB_CONFIG['REPORTS_SCHEMA'],
            '$report_date_value': str(datetime.now().date()),
            '$filtered_start_date_value': params['start_date'],
            '$filtered_end_date_value': params['end_date'],
            '$extracted_schema_name_value': raw_data_schema,
            '$report_table_name_value': report_params['report_name']
        }

        raw_query = read_file_data(report_params['report_script_path'], debug=True)
        ready_query = replace_all(raw_query, report_variables, debug=False)
        execute_query(ready_query, debug=True)
    
if __name__ == '__main__':
    start_reports({'calc_id': 1})