package main_test

import (
	"os"
	"os/exec"
	"path/filepath"

	. "github.com/onsi/ginkgo"
	. "github.com/onsi/gomega"

	"testing"
)

func TestConcourseFilter(t *testing.T) {
	RegisterFailHandler(Fail)
	BuildTestBinary(".", "cred-filter")
	RunSpecs(t, "ConcourseFilter Suite")
}

func BuildTestBinary(relativePathToDir, FileName string) {
	dir, err := os.Getwd()
	if err != nil {
		panic(err)
	}

	binaryDestination := filepath.Join(dir, relativePathToDir, FileName+".exe")
	SourceFile := filepath.Join(dir, relativePathToDir, FileName+".go")

	cmd := exec.Command("go", "build", "-o", binaryDestination, SourceFile)
	err = cmd.Run()
	if err != nil {
		panic(err)
	}
}
