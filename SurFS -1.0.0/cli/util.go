package surfs

import (
	"errors"
	"os"
)

func containString(slice []string, item string) bool {
	for _, s := range slice {
		if s == item {
			return true
		}
	}
	return false
}

func saveToFileSafely(path string, data []byte) error {
	tmpPath := path + ".tmp"
	w, err := os.OpenFile(tmpPath, os.O_CREATE|os.O_TRUNC|os.O_WRONLY, 0644)
	if err != nil {
		w.Close()
		return err
	}

	n, err := w.Write(data)
	if err != nil {
		w.Close()
		return err
	}
	if n != len(data) {
		w.Close()
		return errors.New("partial write")
	}

	w.Close()
	// log.Print("write ok")
	err = os.Rename(tmpPath, path)
	if err != nil {
		return err
	}

	// log.Print("rename ok")
	return err
}

func minInt(a, b int) int {
	if a > b {
		return b
	} else {
		return a
	}
}

func boolString(v bool) string {
	if v == true {
		return "true"
	} else {
		return "false"
	}
}

