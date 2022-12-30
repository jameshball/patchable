- 1.4.0
  - Update design of blocks, inputs and outputs
  - Change how blocks are moved around
    - Instead of holding Ctrl, just click and drag with right click
  - Allow clicking and dragging with right click to select multiple blocks at a time and move them

- 1.3.0
  - Add `.patchable` project files, allowing you to save and load the state of your patch
  - Currently, the file name is `project.patchable` and is saved with Ctrl+S and loaded with Ctrl+O

- 1.2.1
  - Massively simplify all code for Blocks
  - Correctly index outputs for blocks with multiple outputs

- 1.2.0
  - Add AddBlock
  - Add MultiplyBlock
  - Allow blocks to be added with Shift to open a context menu where you can choose blocks
  - Add input and output ports to blocks that you can connect between
  - Right click an input port to remove the connection

- 1.1.0
  - Add SpinnerBlock
  - Add SliderBlock
  - Wire up blocks to correctly output audio
  - Change movement of blocks to only happen when holding Ctrl/Cmd so that spinners/sliders can be interacted with
  - Hold right click to draw a cable between blocks

- 1.0.0
  - Initial release!
  - Basic building blocks for patching and testing audio connection :)