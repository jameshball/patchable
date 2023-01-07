# Patchable

![patchable gui](https://user-images.githubusercontent.com/38670946/211172516-3a6423e9-bfdb-4e15-851a-7a333560ac4c.png)

Patchable is an early-stage development node-based visual audio programming platform, like Pure Data or Max/MSP, but developed from the ground-up with user-friendliness first and foremost.

This is being built to be integrated with [osci-render](https://github.com/jameshball/osci-render), an oscilloscope music synthesizer, as a user-friendly option to make custom patches and audio effects.

Patchable is also runnable as a fully-functional standalone application, but when complete it will have inhrently fewer features than the osci-render version.

To try out Patchable standalone, just navigate to the [latest release](https://github.com/jameshball/patchable/releases). Instructions for installing and running are similar to [osci-render's](https://github.com/jameshball/osci-render#installing).

I'm developing the following features before I implement it in osci-render:

### Hard or Time-Consuming

- [x] Basic node-based interface with functional cables between nodes
- [x] Saving and loading project files
- [ ] Merging groups of blocks into modules (half complete)
- [ ] MANY more blocks, e.g. conditional/comparative, various math functions, high/low pass filters, and more

### Medium or Unsure

- [ ] Zooming in/out
- [ ] Support for custom external inputs (i.e. audio from osci-render)
- [ ] Importing modules from other project files

### Easy

- [x] Adding and removing blocks
