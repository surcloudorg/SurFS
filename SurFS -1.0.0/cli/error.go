/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package surfscli

import (
	"errors"
	"fmt"
)

var (
	ErrInvalidConfig       error = errors.New("invalid config")
	ErrPoolNotFound        error = errors.New("pool not found")
	ErrPoolFailure         error = errors.New("pool failed")
	ErrPoolDisconnected    error = errors.New("pool disconnected")
	ErrPoolNameNotMatch    error = errors.New("pool name not match to config")
	ErrVolumeNotFound      error = errors.New("volume not found")
	ErrSnapshotNotFound    error = errors.New("snapshot not found")
	ErrDuplicatedVolName   error = errors.New("multiple volumes have same name")
	ErrVolumeAlreadyExists error = errors.New("volume already exists")
	ErrNoSpace             error = errors.New("no space to create volume")
)

// An error returned by fs server. We add extra information for
// debugging.
type ServerError struct {
	Server  fsServer
	Status  int
	Message string
}

func (s *ServerError) Error() string {
	return fmt.Sprintf("Server: %s; Status: %d; message: %s",
		s.Server.HttpAddr, s.Status, s.Message)
}

func NewServerError(server fsServer, status int, msg string) *ServerError {
	return &ServerError{server, status, msg}
}

// Errors returned by multiple servers. For example, we broadcast a
// request to all servers, then multiple servers may return different
// error messages.
type MultipleServerError struct {
	Errors []*ServerError
}
