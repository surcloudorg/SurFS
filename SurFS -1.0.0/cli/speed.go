package surfs

// HostIP
//	This is an IP address indicate who will use the volume created by
//	SurFS. We use this IP address to select a best SurFS server to
//	create volume.

// Speed estimation
//	To maximine IO performance, we try to create a volume on a server
//	which is "closest" to the "hostip". By now, we use a simple ping to
//	estimate the latency between server and hostip.

import (
	"net/url"
)

type pingResp struct {
	Latency float64 `json:"latency"`
}

const veryLargeLatency = float64(100000000000)

func (c *Client) getLatency(server fsServer, hostip string) float64 {
	var resp = pingResp{}
	form := url.Values{
		"ip": {hostip},
	}
	err := c.doPostRequest(server, "service/block/system/speed", form, &resp)
	if err != nil {
		c.logger.Printf("failed to ping %s: %v", hostip, err)
		return veryLargeLatency
	}

	if resp.Latency <= 0 {
		return veryLargeLatency
	} else {
		return resp.Latency
	}
}
