# Concourse Filter
Redacts Stdout text that contain text from env variables

## Compile
```bash
$ go build
```

## Usage
```bash
$ export PASSWORD=password
$ ./test.sh |& ./concourse-filter
```

### Sample output
Without the filter
```bash
➜  concourse-filter git:(master) ✗ ./test.sh
start test
password
dpasswordd
ls: password: No such file or directory
./test.sh: line 7: ech: command not found
stop test password
```


With the filter
```bash
➜  concourse-filter git:(master) ✗ ./test.sh |& ./concourse-filter
start test
[redacted]
d[redacted]d
ls: [redacted]: No such file or directory
./test.sh: line 7: ech: command not found
stop test [redacted]
```

## Testing
```bash
go get ./...
ginkgo
```
