#!/usr/bin/python3

import urllib.request
from os import getcwd
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

url = 'https://bulk.openweathermap.org/sample/city.list.min.json.gz'
urllib.request.urlretrieve(url, f'{getcwd()}/city.list.min.json.gz')

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
                state_id INTEGER,
                countryId INTEGER,
                lat REAL NOT NULL,
                lon REAL NOT NULL,
                FOREIGN KEY (state_id) REFERENCES states (id),
                FOREIGN KEY (countryId) REFERENCES countries (id))''')

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
                    (country_count, country)
                )
                country_count += 1
            country_id = countries[country]

        cur.execute(
            "INSERT INTO cities VALUES (?, ?, ?, ?, ?, ?)",
            (c[ID], c[NAME], state_id,
                country_id, c[COORD][LAT], c[COORD][LON])
        )

con.commit()
con.close()
