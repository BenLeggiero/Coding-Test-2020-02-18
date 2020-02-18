# [Coding Test 2020-02-18](https://github.com/BenLeggiero/Coding-Test-2020-02-18) #

A coding test I performed on 2020-02-18


## `// TODO:` ##

Here are things I started working on, or intended to work on, but which are not complete at time of submission:

* HLS Video Player
    * The requirement to use a video player which can play HLS content was notably complex; Android comes with a built-in video player widget that can handle MPEG content (`mp4`, `h.264`, et al), but it cannot yet play HLS content. Because of this, using [Google's `ExoPlayer`](https://github.com/google/ExoPlayer) involved a lot of boilerplate and setup that couldn't be integrated into this app in one workday.
    * See branch [`feature/HLS-Video-Player`](https://github.com/BenLeggiero/Coding-Test-2020-02-18/tree/feature/HLS-Video-Player) for the progress I made before the end of the test
* Documentation
    * Normally, I document every single API I write. However, because of the time constraints of this assessment, I forewent documentation to rebalance the Quality-Time-Budget triangle closer to Time
