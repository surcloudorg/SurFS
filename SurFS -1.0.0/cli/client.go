package surfs

import (
	"encoding/json"
	"errors"
	"fmt"
	"io/ioutil"
	"log"
	"net/http"
	"net/url"
	"os"
	"path"
	"strings"
	"sync"
	"time"
)

const (
	affiliationFile string = "affiliation.json"
	poolStateFile   string = "pool_state.json"
)

type jsonErrorHeader struct {
	Status   int    `json:"status"`
	Response string `json:"response"`
}

type fsServer struct {
	HttpAddr string   `json:"-"`
	Pools    []string `json:"pools"`
}

// This is the all-in-one SurFS client.
type Client struct {
	lock   sync.Mutex
	config *Config
	client *http.Client
	logger *log.Logger

	servers     []fsServer
	affiliation *affiliationMgr
	poolStates  *poolStateManager
}

func (c *Client) GetConfig() *Config {
	return c.config
}

// Create a new client using given configuration. By now, we have
// NOT connected to SurFS, you should call "ConnectToSurFS()"
// immediately.
func NewClient(config *Config) (*Client, error) {
	c := new(Client)
	c.config = config
	c.affiliation = newAffiliationMgr()
	c.poolStates = newPoolStateManager()

	// Create work dir
	info, err := os.Stat(config.WorkDir)
	if err != nil {
		if !os.IsNotExist(err) {
			return nil, errors.New("failed to create workdir")
		}
		err = os.MkdirAll(config.WorkDir, 0755)
		if err != nil {
			return nil, errors.New("failed to create workdir")
		}
	} else {
		if !info.IsDir() {
			return nil, errors.New("failed to create workdir")
		}
	}

	// Load pool state if exists
	b, err := ioutil.ReadFile(path.Join(config.WorkDir, poolStateFile))
	if err != nil {
		if !os.IsNotExist(err) {
			return nil, err
		}
	} else {
		err := c.poolStates.Unmarshal(b)
		if err != nil {
			return nil, err
		}
	}

	// Load affiliation if exists
	b, err = ioutil.ReadFile(path.Join(config.WorkDir, affiliationFile))
	if err != nil {
		if !os.IsNotExist(err) {
			return nil, err
		}
	} else {
		err := c.affiliation.Unmarshal(b)
		if err != nil {
			return nil, err
		}
	}

	// HTTP configuration
	c.client = &http.Client{Timeout: time.Second * time.Duration(config.Timeout)}
	if len(config.Proxy) != 0 {
		proxyUrl, err := url.Parse(config.Proxy)
		if err == nil {
			c.client.Transport = &http.Transport{Proxy: http.ProxyURL(proxyUrl)}
		}
	}

	// Log configuration
	logFlags := log.Ldate | log.Ltime | log.LUTC | log.Lshortfile
	if len(config.LogFile) > 0 {
		logFile, err := os.OpenFile(config.LogFile,
			os.O_WRONLY|os.O_APPEND|os.O_CREATE, 0644)
		if err != nil {
			return nil, err
		}
		c.logger = log.New(logFile, "", logFlags)
	} else {
		c.logger = log.New(os.Stdout, "", logFlags)
	}

	return c, nil
}

// This function connects to SurFS cluster, and returns error if failed
func (c *Client) ConnectToSurFS() error {
	fsInfo, err := c.GetInfo()
	if err != nil {
		return err
	}

	c.servers = fsInfo.Servers
	poolNames := make([]string, 0)
	for _, p := range c.servers {
		poolNames = append(poolNames, p.Pools...)
	}
	c.poolStates.removeNonExistedPool(poolNames)
	return nil
}

func (c *Client) savePoolState() error {
	b, err := c.poolStates.Marshal()
	if err != nil {
		return err
	}
	return saveToFileSafely(path.Join(c.config.WorkDir, poolStateFile), b)
}

func (c *Client) doGetRequest(server fsServer, api string, out interface{}) error {
	if len(server.HttpAddr) == 0 {
		panic("empty server address")
	}
	fullurl := server.HttpAddr + "/" + api
	req, err := http.NewRequest("GET", fullurl, nil)
	if err != nil {
		c.logger.Printf("failed to create request: %s\n", err)
		return err
	}

	req.Header.Set("X-SurFS-Client", "SurFS-SDK-Go")

	if c.config.Debug {
		dumpRequest(req, os.Stdout)
	}

	resp, err := c.client.Do(req)
	if err != nil {
		c.logger.Printf("failed to send request to server: %s\n", err)
		return err
	}

	return c.parseResponse(server, resp, out)
}

func (c *Client) doPostRequest(server fsServer, api string,
	form url.Values, out interface{}) error {
	if len(server.HttpAddr) == 0 {
		panic("empty server address")
	}

	fullurl := server.HttpAddr + "/" + api

	r := strings.NewReader(form.Encode())
	req, err := http.NewRequest("POST", fullurl, r)
	if err != nil {
		c.logger.Printf("failed to create request: %s\n", err)
		return err
	}

	req.Header.Set("Content-Type", "application/x-www-form-urlencoded")
	req.Header.Set("X-SurFS-Client", "SurFS-SDK-Go")

	if c.config.Debug {
		dumpRequest(req, os.Stdout)
	}

	resp, err := c.client.Do(req)
	if err != nil {
		c.logger.Printf("failed to send request to server: %s\n", err)
		return err
	}

	return c.parseResponse(server, resp, out)
}

func (c *Client) parseResponse(server fsServer,
	resp *http.Response, out interface{}) error {

	if c.config.Debug {
		dumpResponse(resp, os.Stdout)
	}

	defer resp.Body.Close()
	if resp.StatusCode < 200 || resp.StatusCode > 299 {
		body, err := ioutil.ReadAll(resp.Body)
		if err != nil {
			c.logger.Printf("failed to read request body: %s\n", err)
			return err
		}
		c.logger.Printf("server returns: %s - %s", resp.Status, string(body))
		trimBody := body[:minInt(len(body), 32)]
		return fmt.Errorf("http error %s: %s", resp.Status, string(trimBody))
	}

	body, err := ioutil.ReadAll(resp.Body)
	if err != nil {
		c.logger.Printf("failed to read request body: %s\n", err)
		return err
	}

	// Check status code in json
	if len(body) > 0 {
		var header jsonErrorHeader
		err = json.Unmarshal(body, &header)
		if err != nil {
			trimBody := body[:minInt(len(body), 32)]
			c.logger.Printf("failed to Unmarshal: %s \n %s\n", err, string(trimBody))
			return err
		}

		if header.Status != 0 {
			return NewServerError(server, header.Status, header.Response)
		}
	}

	// Ignore response
	if out == nil {
		return nil
	}

	err = json.Unmarshal(body, out)
	if err != nil {
		c.logger.Printf("failed to Unmarshal: %s \n %s\n", err, string(body))
		return err
	}

	return nil
}
