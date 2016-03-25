package surfs

type server struct {
	IP    string   `json:"ip"`
	Pools []string `json:"pools"`
}
type surfsInfoResp struct {
	Infos   []server `json:"infos"`
	Version string   `json:"version"`
}

type poolConnectInfo struct {
	Pool      string `json:"pool"`
	Connected bool   `json:"connected"`
}

type FsInfo struct {
	Servers   []fsServer        `json:"-"`
	Pools     []poolConnectInfo `json:"pools"`
	FsVersion string            `json:"version"`
}

// Get information of SurFS cluster.
func (c *Client) GetInfo() (*FsInfo, error) {
	tmpServer := fsServer{c.config.EntryPoint, nil}
	resp := &surfsInfoResp{}
	err := c.doGetRequest(tmpServer, "service/block/system/surfs", &resp)
	if err != nil {
		return nil, err
	}
	fsInfo := &FsInfo{make([]fsServer, 0), make([]poolConnectInfo, 0),
		resp.Version}

	for _, i := range resp.Infos {
		s := fsServer{}
		s.HttpAddr = i.IP
		s.Pools = i.Pools

		fsInfo.Servers = append(fsInfo.Servers, s)

		for _, p := range i.Pools {
			fsInfo.Pools = append(fsInfo.Pools,
				poolConnectInfo{p, c.poolStates.poolConnected(p)})
		}
	}

	return fsInfo, nil
}

// Get SurFS version.
func (c *Client) FsVersion() (version string, err error) {
	info, err := c.GetInfo()
	if err != nil {
		return "", err
	}
	return info.FsVersion, nil
}

func (c *Client) poolExists(pool string) bool {
	for _, s := range c.servers {
		for _, p := range s.Pools {
			if pool == p {
				return true
			}
		}
	}
	return false
}
