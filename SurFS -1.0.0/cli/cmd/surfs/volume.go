package main

import (
	"errors"
	"github.com/codegangsta/cli"
	"strings"
	"surfs"
)

var volumeCommands = []cli.Command{
	{
		Name:        "list",
		Usage:       "List all volumes in a pool",
		ArgsUsage:   "<poolname>",
		Description: "list volumes of pool",
		Action:      cmdListVolumesInPool,
	},

	{
		Name:        "volume",
		Usage:       "Get volume information",
		ArgsUsage:   "<volname>",
		Description: "volume commands",
		Action:      cmdLocateVolume,
	},

	{
		Name:      "create",
		Usage:     "Create a volume",
		ArgsUsage: "-V 10G [-P hostip] <volname>",
		Flags: []cli.Flag{
			cli.StringFlag{
				Name:  "V",
				Usage: "volume size",
			},
			cli.StringFlag{
				Name:  "P",
				Usage: "host ip",
			},
		},
		Description: "create volume",
		Action:      cmdCreateVolume,
	},

	{
		Name:      "copy",
		Usage:     "Copy a volume",
		ArgsUsage: "<src_volume> <dest_volume>",
		Flags: []cli.Flag{
			cli.BoolFlag{
				Name:  "f",
				Usage: "force overwrite if dest_volume exists",
			},
		},
		Description: "copy volume",
		Action:      cmdCopyVolume,
	},

	{
		Name:      "copy_progress",
		Usage:     "Query copy volume progress",
		ArgsUsage: "<dest_volume>",
		Action:    cmdCopyVolumeProgress,
	},

	{
		Name:        "delete",
		Usage:       "Delete volume or snapshot",
		ArgsUsage:   "<volume_name|snapshot_name>",
		Description: "delete volume or snapshot",
		Action:      cmdDeleteVolumeOrSnap,
	},

	/*
		{
			Name:        "delete_volume",
			Usage:       "Delete volume",
			ArgsUsage:   "<volume_name>",
			Description: "delete volume",
			HideHelp:    true,
			Action:      cmdDeleteVolume,
		},
	*/

	{
		Name:        "export",
		Usage:       "Create iSCSI target for a volume",
		ArgsUsage:   "<iqn_name> <initiator_name> <username:password> <volume_name>",
		Description: "export volume",
		Action:      cmdExportVolume,
	},

	{
		Name:        "check_export",
		Usage:       "Check the iSCSI target status of a volume",
		ArgsUsage:   "<iqn_name> <initiator_name> <username:password> <volume_name>",
		Description: "check and fix volume exportion",
		Action:      cmdCheckExportVolume,
	},

	{
		Name:        "disexport",
		Usage:       "Delete the iSCSI target of volume",
		ArgsUsage:   "<volume_name>",
		Description: "undo export volume",
		Action:      cmdUndoExport,
	},
}

func cmdLocateVolume(c *cli.Context) {
	client := createClient(c)
	if len(c.Args()) == 0 {
		printErrorAndExit("error: no volume name")
	}

	_, vol, err := client.LocateVolume(c.Args().First())
	if err != nil {
		printErrorAndExit("error: %s", err)
	} else {
		printData(vol)
	}
}

func cmdCreateVolume(c *cli.Context) {
	client := createClient(c)
	if !c.IsSet("V") {
		printErrorAndExit("error: no volume size")
	}

	sizeStr := c.String("V")
	vsize, err := parseBytes(sizeStr)
	if err != nil || vsize <= 0 {
		printErrorAndExit("error: invalid volume size")
	}

	var hostip string
	if c.IsSet("P") {
		hostip = c.String("P")
	}

	if len(c.Args()) == 0 {
		printErrorAndExit("error: no volume name")
	}

	arg := c.Args().First()
	volname := ""
	poolname := ""
	backslash := strings.IndexByte(arg, '/')
	if backslash == -1 {
		poolname = ""
		volname = arg
	} else {
		poolname = arg[:backslash]
		volname = arg[backslash+1:]
	}

	v, err := client.CreateVolume(poolname, volname, vsize, hostip)
	if err != nil {
		printErrorAndExit("error: %s", err)
	} else {
		printData(v)
	}
}

func cmdListVolumesInPool(c *cli.Context) {
	client := createClient(c)
	if len(c.Args()) == 0 {
		printErrorAndExit("error: no pool name")
	}

	pool := c.Args().First()
	vols, err := client.GetVolumeListOfPool(pool)
	if err != nil {
		printErrorAndExit("error: %s", err)
	} else {
		printData(vols)
	}
}

/*
<iqn_name>
<initiator_name>
<chap_info>: username:password
<volume_name>
<host_ip>
*/
func cmdExportVolume(c *cli.Context) {
	client := createClient(c)

	req, err := parseExportArgs(c.Args())
	if err != nil {
		printErrorAndExit("error: %v", err)
	}

	server, err := client.ExportVolume(req)
	if err != nil {
		printErrorAndExit("error: %s", err)
	} else {
		type exportInfo struct {
			IP string `json:"ip"`
		}

		printData(&exportInfo{server.IP})
	}
}

/*
<iqn_name>
<initiator_name>
<chap_info>: username:password
<volume_name>
<host_ip>
*/
func cmdCheckExportVolume(c *cli.Context) {
	client := createClient(c)

	req, err := parseExportArgs(c.Args())
	if err != nil {
		printErrorAndExit("error: %v", err)
	}

	server, result, err := client.CheckExportVolume(req)
	if err != nil {
		printErrorAndExit("error: %s", err)
	} else {
		status := ""
		if len(result.Repaired) == 0 {
			status = "ok"
		} else if hasString(result.Repaired, "target") {
			status = "target"
		} else if hasString(result.Repaired, "initiator") {
			status = "initiator"
		} else {
			status = result.Repaired[0]
		}

		type repaireInfo struct {
			Repaired string `json:"repaired"`
			IP       string `json:"ip"`
		}

		printData(&repaireInfo{status, server.IP})
	}
}

func cmdUndoExport(c *cli.Context) {
	client := createClient(c)

	args := c.Args()
	if len(args) == 0 {
		printErrorAndExit("error: no volume name")
	}

	volname := c.Args().First()
	server, err := client.UndoExportVolume(volname)
	if err != nil {
		printErrorAndExit("error: %s", err)
	} else {
		type undoExportInfo struct {
			IP string `json:"ip"`
		}

		printData(&undoExportInfo{server.IP})
	}
}

func cmdDeleteVolume(c *cli.Context) {
	client := createClient(c)

	args := c.Args()
	if len(args) == 0 {
		printErrorAndExit("error: no volume name")
	}

	volname := c.Args().First()
	server, err := client.DeleteVolume(volname)
	if err != nil {
		printErrorAndExit("error: %s", err)
	} else {
		type deleteVolumeInfo struct {
			IP string `json:"ip"`
		}
		printData(&deleteVolumeInfo{server.IP})
	}
}

func cmdDeleteVolumeOrSnap(c *cli.Context) {
	client := createClient(c)

	args := c.Args()
	if len(args) == 0 {
		printErrorAndExit("error: no volume/snapshot name")
	}

	type deleteVolumeInfo struct {
		IP string `json:"ip"`
	}

	volname := c.Args().First()
	server, err := client.DeleteVolume(volname)
	if err == nil {
		printData(&deleteVolumeInfo{server.IP})
		return
	}

	if err != surfs.ErrVolumeNotFound {
		goto printError
	} else {
		// Maybe this is a snapshot?
		server, err = client.DeleteSnapshot(volname)
		if err == nil {
			printData(&deleteVolumeInfo{server.IP})
			return
		} else {
			if err == surfs.ErrSnapshotNotFound {
				err = errors.New("volume/snapshot not found")
			}
		}
	}

printError:
	printErrorAndExit("error: %s", err)
}

func cmdCopyVolume(c *cli.Context) {
	client := createClient(c)

	args := c.Args()
	if len(args) < 2 {
		printErrorAndExit("error: no src/dest volume name")
	}

	overwrite := c.Bool("f")
	src := c.Args()[0]
	dest := c.Args()[1]
	server, err := client.CopyVolume(src, dest, overwrite)
	if err != nil {
		printErrorAndExit("error: %s", err)
	} else {
		type copyVolumeInfo struct {
			IP string `json:"ip"`
		}

		printData(&copyVolumeInfo{server.IP})
	}
}

func cmdCopyVolumeProgress(c *cli.Context) {
	client := createClient(c)

	args := c.Args()
	if len(args) < 1 {
		printErrorAndExit("error: no dest volume name")
	}

	dest := c.Args()[0]
	prg, err := client.GetCopyVolumeProgress(dest)
	if err != nil {
		printErrorAndExit("error: %s", err)
	} else {
		type copyVolumeProgress struct {
			Progress string `json:"progress"`
		}

		printData(&copyVolumeProgress{surfs.ProgressToString(prg)})
	}
}
