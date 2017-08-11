CREATE TABLE senate_election (
  id TEXT PRIMARY KEY,
  name TEXT,
  aec_id INTEGER,
  date DATE
);

INSERT INTO senate_election(id, aec_id, date, name) VALUES
  ('2016', '20499', '2016-07-02', '2016 election'),
  ('2014WA', '17875', '2014-04-05', '2014 WA Senate election'),
  ('2013', '17496', '2013-09-07', '2013 election');

CREATE TABLE state (
  abbreviation VARCHAR(3) PRIMARY KEY,
  name VARCHAR(28),
  territory BOOLEAN DEFAULT FALSE
);

INSERT INTO state(abbreviation, name) VALUES
  ('NSW', 'New South Wales'),
  ('QLD', 'Queensland'),
  ('SA', 'South Australia'),
  ('TAS', 'Tasmania'),
  ('VIC', 'Victoria'),
  ('WA', 'Western Australia');

INSERT INTO state(abbreviation, name, territory) VALUES
  ('NT', 'Northern Territory', TRUE),
  ('ACT', 'Australian Capital Territory', TRUE);

CREATE TABLE division (
  id BIGINT PRIMARY KEY,
  election VARCHAR(5) REFERENCES senate_election(id),
  aec_id INTEGER,
  state VARCHAR(3) REFERENCES state(abbreviation) ,
  name VARCHAR
);

CREATE UNIQUE INDEX uk_division_name ON division(LOWER(name));

CREATE TABLE total_formal_ballot_count (
  id SERIAL PRIMARY KEY,

  total_formal_ballots INTEGER,
  ordinal_nationally INTEGER,

  -- TODO unneeded?
  ordinal_state INTEGER,
  ordinal_division INTEGER
);

CREATE TABLE address (
  id SERIAL PRIMARY KEY,

  lines TEXT[],

  suburb TEXT,
  postcode CHAR(4),
  state VARCHAR(3) REFERENCES state(abbreviation)
);

CREATE TYPE VOTE_COLLECTION_POINT_TYPE AS ENUM ('polling_place', 'absentee', 'postal', 'prepoll', 'provisional');
CREATE TYPE POLLING_PLACE_TYPE AS ENUM ('polling_place', 'special_hospital_team', 'remote_mobile_team', 'other_mobile_team', 'pre_poll_voting_centre');

CREATE TABLE vote_collection_point (
  id SERIAL PRIMARY KEY,

  election VARCHAR(5) REFERENCES senate_election(id),
  state VARCHAR(3) REFERENCES state(abbreviation),
  division INTEGER REFERENCES division(id),

  type VOTE_COLLECTION_POINT_TYPE,
  name VARCHAR,

  -- Only if this is not a polling place
  number INTEGER,

  -- Only if this is a polling place

  aec_id INTEGER,

  polling_place_type POLLING_PLACE_TYPE,

  multiple_locations BOOLEAN,

  premises_name VARCHAR,
  address INTEGER REFERENCES address(id),

  latitude DOUBLE PRECISION,
  longitude DOUBLE PRECISION
);

CREATE TABLE stat (
  id SERIAL PRIMARY KEY,

  stat_class VARCHAR,

  election VARCHAR(5) REFERENCES senate_election(id),
  state VARCHAR(3) REFERENCES state(abbreviation),
  division INTEGER REFERENCES division(id),
  vote_collection_point INTEGER REFERENCES vote_collection_point(id),

  amount DOUBLE PRECISION,
  per_capita DOUBLE PRECISION
);

CREATE TABLE rank (
  id SERIAL PRIMARY KEY,

  stat INTEGER REFERENCES stat(id),

  jurisdiction_level VARCHAR,

  ordinal INTEGER,
  ordinal_per_capita INTEGER,

  total_count INTEGER
);