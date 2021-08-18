#!/usr/bin/python3

import urllib.request
from os import getcwd, remove
from os.path import isfile
import shutil
import unidecode
import sqlite3
import gzip
import json

ID = "id"
NAME = "name"
STATE = "state"
STATE_ID = "stateId"
COUNTRY = "country"
COUNTRY_ID = "countryId"
LAT = "lat"
LON = "lon"
COORD = "coord"

assets_path = f'{getcwd()}/../app/src/main/assets/cities.db'
db_path = f'{getcwd()}/cities.db'

if isfile(db_path):
    remove(db_path)

url = 'https://bulk.openweathermap.org/sample/city.list.min.json.gz'
dl_path = f'{getcwd()}/city.list.min.json.gz'
if not isfile(dl_path):
    urllib.request.urlretrieve(url, dl_path)

con = sqlite3.connect('cities.db')
cur = con.cursor()

cur.execute('''CREATE TABLE states(
                id INTEGER NOT NULL PRIMARY KEY,
                name TEXT NOT NULL)''')

cur.execute('''CREATE TABLE countries(
                id INTEGER NOT NULL PRIMARY KEY,
                name TEXT NOT NULL)''')

cur.execute('''CREATE TABLE cities(
                id INTEGER NOT NULL PRIMARY KEY,
                name TEXT NOT NULL,
                stateId INTEGER,
                countryId INTEGER,
                lat REAL NOT NULL,
                lon REAL NOT NULL,
                FOREIGN KEY (stateId) REFERENCES states (id),
                FOREIGN KEY (countryId) REFERENCES countries (id))''')

cur.execute('''CREATE TABLE saved_cities(
                id INTEGER NOT NULL PRIMARY KEY,
                selected INTEGER NOT NULL,
                FOREIGN KEY (id) REFERENCES cities (id))''')

# Fake city for current location
cur.execute(
    "INSERT INTO cities VALUES (?, ?, ?, ?, ?, ?)",
    (0, "", None, None, -1.0, -1.0)
)

cur.execute(
    "INSERT INTO saved_cities VALUES (?, ?)",
    (0, True)
)

country_names = {}

with open('countries.csv', 'r') as fin:
    for line in fin.readlines():
        code, name = line.strip().split("|")
        country_names[code] = name

with gzip.open('city.list.min.json.gz', 'r') as fin:
    cities = json.load(fin)

    states = {}
    state_count = 1
    countries = {}
    country_count = 1

    for c in cities:
        state = c[STATE]
        if state == "":
            state_id = None
        else:
            if state not in states:
                states[state] = state_count
                cur.execute(
                    "INSERT INTO states VALUES (?, ?)",
                    (state_count, state)
                )
                state_count += 1
            state_id = states[state]

        country = c[COUNTRY]
        if country == "":
            country_id = None
        else:
            if country not in countries:
                countries[country] = country_count
                cur.execute(
                    "INSERT INTO countries VALUES (?, ?)",
                    (country_count, country_names[country])
                )
                country_count += 1
            country_id = countries[country]

        cur.execute(
            "INSERT INTO cities VALUES (?, ?, ?, ?, ?, ?)",
            (c[ID], unidecode.unidecode(c[NAME]), state_id,
                country_id, c[COORD][LAT], c[COORD][LON])
        )

con.commit()
con.close()
shutil.copyfile(db_path, assets_path)
