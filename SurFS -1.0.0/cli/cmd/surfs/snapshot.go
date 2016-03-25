package main

import (
	"github.com/codegangsta/cli"
)

var snapCommands = []cli.Command{
	{
		Name:        "snap",
		Usage:       "Create a snapshot",
		ArgsUsage:   "<volume_name> <snapshot_name>",
		Description: "create snapshot of volume",
		Action:      cmdCreateSnapshot,
	},

	{
		Name:      "snaplist",
		Usage:     "List snapshots in a pool",
		ArgsUsage: "<pool_name>",
		Flags: []cli.Flag{
			cli.StringFlag{
				Name:  "S",
				Usage: "snapshot name, just query for this snapshot",
			},
		},
		Description: "query for snapshot information",
		Action:      cmdListSnapshot,
	},

	/*
	{
		Name:        "delete_snap",
		Usage:       "Delete a volume or snapshot",
		ArgsUsage:   "<snapshot_name>",
		Description: "delete snapshot information",
		HideHelp:    true,
		Action:      cmdDeleteSnap,
	},
	*/

	{
		Name:        "snap_to_volume",
		Usage:       "Create volume from snapshot",
		ArgsUsage:   "<snapshot_name> <volumename> <volume_size>",
		Description: "create volume from snapshot",
		Action:      cmdSnapToVolume,
	},
}

func cmdCreateSnapshot(c *cli.Context) {
	client := createClient(c)

	args := c.Args()
	if len(args) < 1 {
		printErrorAndExit("error: no volume name")
	}

	if len(args) < 2 {
		printErrorAndExit("error: no snapshot name")
	}

	volname := c.Args()[0]
	snapname := c.Args()[1]
	_, snap, err := client.CreateSnapshot(volname, snapname)
	if err != nil {
		printErrorAndExit("error: %s", err)
	} else {
		printData(snap)
	}
}

func cmdListSnapshot(c *cli.Context) {
	client := createClient(c)

	args := c.Args()
	if len(args) < 1 {
		printErrorAndExit("error: no pool name")
	}

	snapname := c.String("S")
	poolname := c.Args().First()
	snaps, err := client.ListSnapshot(poolname, snapname)
	if err != nil {
		printErrorAndExit("error: %s", err)
	} else {
		printData(snaps)
	}
}

func cmdDeleteSnap(c *cli.Context) {
	client := createClient(c)

	args := c.Args()
	if len(args) < 1 {
		printErrorAndExit("error: no snapname name")
	}

	snapname := c.Args().First()
	server, err := client.DeleteSnapshot(snapname)
	if err != nil {
		printErrorAndExit("error: %s", err)
	} else {
		type snapshotInfo struct {
			IP string `json:"ip"`
		}

		printData(&snapshotInfo{server.IP})
	}
}

func cmdSnapToVolume(c *cli.Context) {
	client := createClient(c)

	args := c.Args()
	if len(args) < 1 {
		printErrorAndExit("error: no snapname name")
	}
	if len(args) < 2 {
		printErrorAndExit("error: no volume name")
	}
	if len(args) < 3 {
		printErrorAndExit("error: no volume size")
	}

	snapname := c.Args()[0]
	volname := c.Args()[1]
	size, err := parseBytes(c.Args()[2])
	if err != nil || size <= 0 {
		printErrorAndExit("error: invalid volume size")
	}

	server, err := client.SnapshotToVolume(snapname, volname, int64(size))
	if err != nil {
		printErrorAndExit("error: %s", err)
	} else {
		type snapshotInfo struct {
			IP string `json:"ip"`
		}

		printData(&snapshotInfo{server.IP})
	}
}
