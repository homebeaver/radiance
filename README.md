# Radiance libraries

Radiance is a collection of libraries for writing modern, elegant and fast Swing applications. It is built with Java 9, and runs on Java 9 and later.

<p align="center">
<img src="https://raw.githubusercontent.com/kirill-grouchnikov/radiance/sunshine/docs/images/theming/skins/nebula1.png" width="340" height="258" border=0>
<img src="https://raw.githubusercontent.com/kirill-grouchnikov/radiance/sunshine/docs/images/theming/skins/gemini2.png" width="340" height="258" border=0>
</p>

<p align="center">
<img src="https://raw.githubusercontent.com/kirill-grouchnikov/radiance/sunshine/docs/images/theming/skins/graphitechalk1.png" width="340" height="258" border=0>
<img src="https://raw.githubusercontent.com/kirill-grouchnikov/radiance/sunshine/docs/images/theming/skins/nightshade2.png" width="340" height="258" border=0>
</p>

## General questions

[This document](docs/faq.md) is where you may find answers to some of the general questions on Radiance. If there's anything missing there, [let me know](https://github.com/kirill-grouchnikov/radiance/issues).

## Core Java libraries

### Common

[Common](docs/common/common.md) provides APIs to work with images and text that scale with the display resolution. Radiance icons scale to maintain crisp, pixel-perfect visuals for application icons. Font APIs can be used to render platform-consistent textual content across a variety of supported platforms.

### Animation

[Animation](docs/animation/animation.md) provides powerful and flexible animation APIs that scale from simple, single-property cases to complex scenarios that involve multiple animations. It powers all animations in the Radiance libraries.

### Theming

[Theming](docs/theming/overview.md) provides a comprehensive set of APIs for skinning Swing applications, addressing the most demanding, modern design requirements. It comes with built-in support for all core Swing components, and a flexible API surface for rendering third-party / application components.

### Components

[Components](docs/components/overview.md) provides a robust set of additional Swing components that can be used as building blocks for creating modern, rich Swing applications. In addition to providing the powerful command button and breadcrumb bar components, it packs an Office Command Bar (ribbon) container that can host a complex hierarchy of flexible, scalable application control surface.

<img src="https://raw.githubusercontent.com/kirill-grouchnikov/radiance/sunshine/docs/images/components/walkthrough/ribbon/taskbar/taskbar-overflow-popup.png" width="1181" border=0/>

## Core Kotlin libraries

* [Core Swing extensions](docs/kotlin-ext/swing/swing.md) is a set of Kotlin extensions that expose selected core Swing APIs for idiomatic Kotlin usage.

* [Animation extensions](docs/kotlin-ext/animation/animation-ktx.md) exposes a subset of the API surface of Animation as a concise, focused and approachable Kotlin domain-specific language (DSL). It also provides a number of Kotlin extensions for Animation APIs.

* [Theming extensions](docs/kotlin-ext/theming/theming-ktx.md) is a set of Kotlin extensions that provide a more elegant way of working with scoped Radiance theming APIs.

* [Component extensions](docs/kotlin-ext/components/components-ktx.md) exposes a subset of the API surface of Components as a concise, focused and approachable Kotlin domain-specific language (DSL). It also provides a number of Kotlin extensions for Components APIs.

## Tools

* [Laf Benchmark](docs/tools/laf-benchmark/laf-benchmark.md) provides tools for Swing look-and-feel developers to measure the performance of their libraries.

* [Theming Debugger](docs/tools/theming-debugger/theming-debugger.md) is a visual runtime debugger for Radiance look-and-feel.

* [Scheme Editor](docs/tools/scheme-editor/scheme-editor.md) is a tool for creating and editing color scheme lists.

* [Screenshot](docs/tools/screenshot/screenshot.md) has utilities for taking screenshots for Radiance documentation.

* [Shaper Editor](docs/tools/shape-editor/shape-editor.md) can be used creating and editing files that define custom button shapers.

* [SVG Transcoder](docs/tools/svg-transcoder/svg-transcoder.md) enables usage of vector-based icons in Swing applications. SVG content is transcoded offline to Java / Kotlin classes that use pure Java2D canvas operations that do not require expensive runtime overhead of third-party dependencies.

* [SVG Transcoder Plugin](docs/tools/svg-transcoder/svg-transcoder-gradle-plugin.md) is a Gradle plugin that wraps the SVG Transcoder library to allow build-time transcoding of SVG content into Java / Kotlin classes.


## Demos

[This document](docs/demos.md) lists all major demos and samples that showcase how to use various Radiance APIs.

## Radiance artifacts

Radiance artifacts are available in the central Maven repository under `groupId=org.pushing-pixels`

### Core artifacts

* [![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.pushing-pixels/radiance-neon/badge.svg)](https://maven-badges.herokuapp.com/maven-central/org.pushing-pixels/radiance-neon)
`radiance-neon` for [Neon](docs/common/common.md)
* [![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.pushing-pixels/radiance-trident/badge.svg)](https://maven-badges.herokuapp.com/maven-central/org.pushing-pixels/radiance-trident) `radiance-trident` for [Trident](docs/animation/animation.md)
* [![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.pushing-pixels/radiance-substance/badge.svg)](https://maven-badges.herokuapp.com/maven-central/org.pushing-pixels/radiance-substance) `radiance-substance` for [Substance](docs/theming/overview.md)
* [![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.pushing-pixels/radiance-flamingo/badge.svg)](https://maven-badges.herokuapp.com/maven-central/org.pushing-pixels/radiance-flamingo) `radiance-flamingo` for [Flamingo](docs/components/overview.md)
* [![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.pushing-pixels/radiance-substance-extras/badge.svg)](https://maven-badges.herokuapp.com/maven-central/org.pushing-pixels/radiance-substance-extras) `radiance-substance-extras` for [Substance extras](docs/theming/extras.md)

### Kotlin extension artifacts
* [![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.pushing-pixels/radiance-ember/badge.svg)](https://maven-badges.herokuapp.com/maven-central/org.pushing-pixels/radiance-ember) `radiance-ember` for [Ember](docs/kotlin-ext/theming/theming-ktx.md)
* [![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.pushing-pixels/radiance-meteor/badge.svg)](https://maven-badges.herokuapp.com/maven-central/org.pushing-pixels/radiance-meteor) `radiance-meteor` for [Meteor](docs/kotlin-ext/swing/swing.md)
* [![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.pushing-pixels/radiance-plasma/badge.svg)](https://maven-badges.herokuapp.com/maven-central/org.pushing-pixels/radiance-plasma) `radiance-plasma` for [Plasma](docs/kotlin-ext/components/components-ktx.md)
* [![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.pushing-pixels/radiance-torch/badge.svg)](https://maven-badges.herokuapp.com/maven-central/org.pushing-pixels/radiance-torch) `radiance-torch` for [Torch](docs/kotlin-ext/animation/animation-ktx.md)

### Tools artifacts

* [![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.pushing-pixels/radiance-beacon/badge.svg)](https://maven-badges.herokuapp.com/maven-central/org.pushing-pixels/radiance-beacon) `radiance-beacon` for [Beacon](docs/tools/theming-debugger/theming-debugger.md)
* [![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.pushing-pixels/radiance-ignite/badge.svg)](https://maven-badges.herokuapp.com/maven-central/org.pushing-pixels/radiance-ignite) `radiance-ignite` for [Ignite](docs/tools/svg-transcoder/svg-transcoder-gradle-plugin.md)
* [![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.pushing-pixels/radiance-photon/badge.svg)](https://maven-badges.herokuapp.com/maven-central/org.pushing-pixels/radiance-photon) `radiance-photon` for [Photon](docs/tools/svg-transcoder/svg-transcoder.md)

### Snapshots

Radiance snapshot artifacts are available in the [Sonatype repository](https://oss.sonatype.org/content/repositories/snapshots/org/pushing-pixels/).

## Building Radiance

[This document](docs/building.md) is an overview of how to build Radiance artifacts locally.

<p align="center">
<img src="https://raw.githubusercontent.com/kirill-grouchnikov/radiance/sunshine/docs/images/icon/radiance_product_512.png" width="256" height="256" border=0>
</p>
