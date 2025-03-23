from config import DB_CONFIG
import psycopg2 

def replace_all(text:str, replaces:dict, debug=False):
    if debug:
        print(f'replacing all in raw: \n{text}')
    for key, val in replaces.items():
        text = text.replace(str(key), str(val))
    if debug:
        print(f'result of replacing: \n{text}')
    return text


def read_file_data(path, debug=False):
    if debug:
        print(f'start reading \'{path}\'')
    with open(path, 'r', encoding='UTF-8') as f:
        data = f.read()
    return data

def execute_query(query:str, debug=False):
    if debug:
        print(f'executing query:\n{query}')
    with psycopg2.connect(database=DB_CONFIG['NAME'],
                           user=DB_CONFIG['USER'],
                             password=DB_CONFIG['PASS'],
                               host=DB_CONFIG['HOST'],
                                 port=DB_CONFIG['PORT']) as conn:
        cursor = conn.cursor()
        cursor.execute(query=query)
        conn.commit()
    