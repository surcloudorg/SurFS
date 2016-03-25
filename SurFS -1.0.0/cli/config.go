package surfs

// SurFS client configation
type Config struct {
	// Entry point server, client will connect to this
	// server to get other information
	// Default is "" (empty)
	EntryPoint string `json:"entryPoint"`

	// A directory to save configation file, and other
	// data files
	// Default is "/etc/surfs/"
	WorkDir string `json:"workDir"`

	// The timeout of _ANY_ network request, _NOT_ the timeout of
	// a single command. For example, "create" command will send
	// multiple requests, each of this request uses this timeout
	// setting.
	// Default is 0: no timeout
	Timeout int `json:"timeout"`

	// If set, client will send all requests throught this proxy.
	// This is useful for debugging.
	// Default is "" (empty)
	Proxy string `json:"proxy"`

	// If true, client will print requeset information for debugging
	// Default is false
	Debug bool `json:"debug"`

	// Where to output logs. If empty, all logs will be printed to
	// stdout. 
	// Default is "/var/log/surfs.log"
	LogFile string `json:"logfile"`
}

// Create a default configuration
func NewDefaultConfig() *Config {
	return &Config{
		EntryPoint: "",
		WorkDir:    "/etc/surfs/",
		Timeout:    0,
		Proxy:      "",
		Debug:      false,
		LogFile:    "/var/log/surfs.log",
	}
}
