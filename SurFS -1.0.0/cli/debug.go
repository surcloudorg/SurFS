package surfs

import "net/http"
import "net/http/httputil"
import "io"
import "bytes"
import "bufio"
import "fmt"
import "sync"

var debugLock sync.Mutex

func printLinesWithPrefix(data []byte, prefix string, w io.Writer) {
	r := bufio.NewReader(bytes.NewReader(data))
	for {
		line, _, err := r.ReadLine()
		if err != nil {
			break
		}

		fmt.Fprintf(w, "%s%s\n", prefix, string(line))
	}
}

func dumpRequest(req *http.Request, w io.Writer) {
	debugLock.Lock()
	defer debugLock.Unlock()
	dump, err := httputil.DumpRequest(req, true)
	if err != nil {
		return
	}

	rnrn := bytes.Index(dump, []byte("\r\n\r\n"))
	if rnrn < 0 {
		return
	}

	printLinesWithPrefix(dump[:rnrn], "> ", w)
	fmt.Print("> \n")
	fmt.Printf("%s\n", string(dump[rnrn+4:]))
}

func dumpResponse(resp *http.Response, w io.Writer) {
	debugLock.Lock()
	defer debugLock.Unlock()
	dump, err := httputil.DumpResponse(resp, true)
	if err != nil {
		return
	}

	rnrn := bytes.Index(dump, []byte("\r\n\r\n"))
	if rnrn < 0 {
		return
	}

	printLinesWithPrefix(dump[:rnrn], "< ", w)
	fmt.Print("< \n")
	fmt.Printf("%s\n", string(dump[rnrn+4:]))
}
