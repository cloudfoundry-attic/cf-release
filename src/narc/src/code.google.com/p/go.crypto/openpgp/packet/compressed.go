// Copyright 2011 The Go Authors. All rights reserved.
// Use of this source code is governed by a BSD-style
// license that can be found in the LICENSE file.

package packet

import (
	"code.google.com/p/go.crypto/openpgp/errors"
	"compress/bzip2"
	"compress/flate"
	"compress/zlib"
	"io"
	"strconv"
)

// Compressed represents a compressed OpenPGP packet. The decompressed contents
// will contain more OpenPGP packets. See RFC 4880, section 5.6.
type Compressed struct {
	Body io.Reader
}

func (c *Compressed) parse(r io.Reader) error {
	var buf [1]byte
	_, err := readFull(r, buf[:])
	if err != nil {
		return err
	}

	switch buf[0] {
	case 1:
		c.Body = flate.NewReader(r)
	case 2:
		c.Body, err = zlib.NewReader(r)
	case 3:
		c.Body = bzip2.NewReader(r)
	default:
		err = errors.UnsupportedError("unknown compression algorithm: " + strconv.Itoa(int(buf[0])))
	}

	return err
}
