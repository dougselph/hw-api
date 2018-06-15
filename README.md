# hw_api

The code in this project accomplishes a few things:

1. Imports person records, in multiple, specific, delimited formats, from several files contained in ./resources/data (**Note**: field order in the input files is fixed.)
2. Outputs person maps to the repl console, ordered according to several sorts
3. Provides a simple API to allow a user to retrieve structured JSON records of persons in several sort orders, and to process POST requests with a delimited person record, as string, in the request body to be added to the temporal data set containing all person records.

## Usage

After cloning or downloading the repo, `cd` into the project directory and start your repl.

```
$> lein deps
$> lein repl
```

### Import

There are three provided input files in `./resources/data/`. File `input1.txt` is pipe-delimited. File `input2.txt` is comma-delimited. File `input3.txt` is space-delimited.

These files, and any other like formatted files you may add to that directory, are automatically imported at startup into the data set (an atom) on which all other features operate. 

### Console Output

When your repl session has started, you can do the following:

```
(require '[hw-api.core :as core :refer :all])
;;nil

(people-by-last-name-desc)
;;[{:last-name "Wilson",
;;  :first-name "Nancy",
;;  :sex "F",
;;  :favorite-color "Red",
;;  :date-of-birth "3/16/1954"}
;; {
;;  ...}]

(people-by-dob-asc)
;;[{:last-name "Starr",
;;  :first-name "Ringo",
;;  :sex "M",
;;  :favorite-color "Black",
;;  :date-of-birth "7/7/1940"}
;; {
;;  ...}]

(people-by-sex-last-name-asc)
;;[{:last-name "Benatar",
;;  :first-name "Pat",
;;  :sex "F",
;;  :favorite-color "Green",
;;  :date-of-birth "1/10/1953"}
;; [
;;  ...}]
```

### API

To start the REST API, require the namespace

`(require '[hw-api.api :as api])`

and call one of

```
(api/launch) ; will default to run the API server on port 3333

(api/launch 3000) ; will run on port 3000
```

Access any of the following URLs (changing the port number as appropriate) to fetch JSON results sorted:


http://localhost:3333/records/gender

http://localhost:3333/records/birthdate

http://localhost:3333/records/name


To add a record to the in-memory data set, make a POST request to

http://localhost:3333/records

with the delimited string in the BODY attribute of the request. Here is a sample call using `wget`:

```
$ wget --body-data="Doo | Scooby | M | Green | 09/13/1969" --method=POST --header="Content-Type: application/json" http://localhost:3000/records
```

If the request is successful, the endpoint will return the added record in the response body, encoded as a JSON object. By default, the above command will save the response body to a file in the current directory named 'records'. 

You can confirm the addition of the record by accessing any of the three GET endpoints above and locating the record you added in the resulting JSON payload.


## Tests

Each of the three namespaces employed in this application has a corresponding test suite. To execute them, from your shell in the project directory run:

`lein test`



## License

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
