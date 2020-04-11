# SwingGraph
Java graph drawing library. It's purpose is to simplify building graphs out of Swing
GUI components. More edge style and automatic layouting support is planned.
## Usage
Build using maven the usual way, or use the pre-compiled jars provided. Add the jar
as a dependency to your project and use the `SwingGraph` component to render graphs.
It's API consists of two main methods:
```
    /**
     * Add a vertex to the panel
     * @param v AWT or Swing component of the vertex
     */
    public void addVertex(Component v)
```
Use this for adding vertices two the graph, vertices can be any Swing component,
including JPanels with child components.
```
    /**
     * Add an edge connecting v1 to v2
     * @param v1 AWT or Swing component of the v1
     * @param v2 AWT or Swing component of the v2
     */
    public void addEdge(Component v1, Component v2)
```
Use this to add a directed edge connecting v1 to v2. Non-directional edges are currently
not supported.

Changing the starting size of vertices can be accomplished by calling `setPreferredSize`
on them before adding them to the graph. Similarly the starting position can be adjusted
using `setLocation`.

An example application called `TestGui` is provided.
## License
SwingGraph is released under the terms of the ISC license, check `license.txt` for more
details.