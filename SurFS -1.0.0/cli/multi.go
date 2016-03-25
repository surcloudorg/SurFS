package surfs

type singleResult struct {
	server fsServer
	data   interface{}
	err    error
}

func collectResult(c <-chan *singleResult, n int) []*singleResult {
	out := make([]*singleResult, 0, n)
	for i := 0; i < n; i++ {
		r, ok := <-c
		if !ok || r == nil {
			return out
		}
		out = append(out, r)
	}
	return out
}
