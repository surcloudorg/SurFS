/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package main

import (
	"encoding/json"
	"errors"
	"fmt"
	"github.com/codegangsta/cli"
	"io/ioutil"
	"os"
	"strconv"
	"strings"
	"surfscli"
)

const (
	bB int64 = 1
	kB int64 = 1024
	mB int64 = kB * kB
	gB int64 = mB * kB
	tB int64 = gB * kB
)

func unitSize(c byte) int64 {
	switch c {
	case 'b', 'B':
		return bB
	case 'k', 'K':
		return kB
	case 'g', 'G':
		return gB
	case 't', 'T':
		return tB
	default:
		return 0
	}
}

func parseBytes(s string) (int64, error) {
	unitIdx := strings.IndexAny(s, "TtGgMmKkBb")
	if unitIdx == -1 {
		return strconv.ParseInt(s, 10, 64)
	} else {
		unit := s[len(s)-1]
		usize := unitSize(unit)
		if usize == 0 {
			return 0, errors.New("invalid unit")
		}

		k, err := strconv.ParseInt(s[:len(s)-1], 10, 64)
		if err != nil {
			return 0, err
		}

		return usize * k, nil
	}
}

func parseChapInfo(chap string) (user, pwd string, err error) {
	colonPos := strings.Index(chap, ":")
	if colonPos == -1 {
		return "", "", errors.New("invalid chap info")
	}

	return chap[:colonPos], chap[colonPos+1:], nil
}

func parseExportArgs(args []string) (*surfscli.VolExportReq, error) {
	if len(args) != 5 && len(args) != 4 {
		return nil, errors.New("error: invalid args\n")
	}

	iqn := args[0]
	initiator := args[1]
	chap := args[2]
	volname := args[3]
	hostip := ""
	if len(args) == 5 {
		hostip = args[4]
	}
	user, pwd, err := parseChapInfo(chap)
	if err != nil {
		return nil, errors.New("error: invalid args\n")
	}

	req := &surfscli.VolExportReq{
		IqnName:       iqn,
		InitiatorName: initiator,
		Chap:          surfscli.ChapInfo{user, pwd},
		VolName:       volname,
		HostIp:        hostip,
	}

	return req, nil
}

func hasString(src []string, toFind string) bool {
	for _, s := range src {
		if s == toFind {
			return true
		}
	}

	return false
}

type errMsg struct {
	Success bool   `json:"success"`
	Message string `json:"message"`
}

func marshalData(v interface{}) string {
	if prettyJson {
		b, _ := json.MarshalIndent(v, "", "    ")
		return string(b)
	} else {
		b, _ := json.Marshal(v)
		return string(b)
	}
}

func printErrorAndExit(format string, args ...interface{}) {
	out := errMsg{
		Success: false,
		Message: fmt.Sprintf(format, args...),
	}

	fmt.Fprintf(os.Stdout, "%s\n", marshalData(out))
	os.Exit(0)
}

type dataMsg struct {
	Success bool        `json:"success"`
	Data    interface{} `json:"data,omitempty"`
}

func printData(data interface{}) {
	out := dataMsg{
		Success: true,
		Data:    data,
	}

	fmt.Fprintf(os.Stdout, "%s\n", marshalData(&out))
	os.Exit(0)
}

func getConfigFileLoc(ctx *cli.Context) string {
	if len(ctx.GlobalString("config")) > 0 {
		return ctx.GlobalString("config")
	} else {
		return defaultConfig
	}
}

func loadConfig(ctx *cli.Context) (*surfscli.Config, error) {
	data := make([]byte, 0)
	data, err := ioutil.ReadFile(getConfigFileLoc(ctx))
	if err != nil {
		return nil, err
	}

	cfg := surfscli.NewDefaultConfig()
	err = json.Unmarshal(data, cfg)
	if err != nil {
		return nil, err
	}
	if len(cfg.EntryPoint) == 0 {
		return nil, errors.New("no entrypoint in config")
	}
	return cfg, nil
}

func adjustConfig(ctx *cli.Context, cfg *surfscli.Config) {
	if ctx.GlobalBool("debug") {
		cfg.Debug = ctx.GlobalBool("debug")
	}

	if len(ctx.GlobalString("proxy")) > 0 {
		cfg.Proxy = ctx.GlobalString("proxy")
	}

	if ctx.GlobalInt("timeout") > 0 {
		cfg.Timeout = ctx.GlobalInt("timeout")
	}

	if len(ctx.GlobalString("log")) > 0 {
		cfg.LogFile = ctx.GlobalString("log")
	}

	if cfg.Debug {
		// Log to stdout in debug mode
		cfg.LogFile = ""
	}
}
