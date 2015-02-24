<img src="https://github.com/ustwo/clockwise/wiki/images/ustwo_clockwise_framework_promo.jpg" width="100%" />

Clockwise is a watch face framework for Android Wear developed by ustwo. It extends the [Android Wear Watch Face API][1] and provides base classes and helpers for quickly and correctly developing watch faces. This includes properly handling the various modes of operation, hardware constraints, changes in date/time/time zone, access to data, and performance considerations.  

### Background  

ustwo worked with Google to develop the [first watch faces on the Android Wear platform][2], and in doing so, we learned a great deal and identified the benefit of extending the existing watch face API into an open source framework. The purpose of Clockwise is to help developers more easily consider the inherent nuances in developing watch faces on the Android Wear platform, including varying hardware specifications and battery life conservation. The goal is that by utilizing the Clockwise development framework in conjunction with the [Watch Face design guidelines][3] (co-created with ustwo), developers can enhance the user's experience on Android Wear.  

### Getting Started  

Have a look at the [Clockwise Wiki][4] for more details on how to integrate Clockwise as well as full documentation and code samples.  

Once you are prepared to build your watch face using Clockwise, head over to our [Clockwise Samples][11] GitHub repository for some fully functional watch faces that we've made available.  

## Releases

Release Notes => [RELEASES.md][7]  
Binary History => [Maven Central Repository][6]

## About ![ustwo](https://media.licdn.com/media/p/4/005/02e/351/2f4017d.png)

ustwo developed the first watch faces on the Android Wear platform, and in doing so, identified the benefit of extending the existing watch face API into an open source framework.

If you would like to contribute to Clockwise, please feel free to fork the repository and submit pull requests. When submitting code, please be sure to follow the [Android Code Style Guidelines][10]. Also, please continue to utilize [GitHub Issues][8] and email us at [clockwise@ustwo.com][9] with any and all feedback. We would love to hear your thoughts on how we can improve Clockwise!  

## License

     The MIT License (MIT)  
      
     Copyright (c) 2015 ustwo studio inc (www.ustwo.com)  
      
     Permission is hereby granted, free of charge, to any person obtaining a copy
     of this software and associated documentation files (the "Software"), to deal
     in the Software without restriction, including without limitation the rights
     to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
     copies of the Software, and to permit persons to whom the Software is
     furnished to do so, subject to the following conditions:  
     
     The above copyright notice and this permission notice shall be included in all
     copies or substantial portions of the Software.  
      
     THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
     IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
     FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
     AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
     LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
     OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
     SOFTWARE.  

[www.ustwo.com]:http://www.ustwo.com
[1]:https://developer.android.com/training/wearables/watch-faces/index.html
[2]:http://wear.ustwo.com
[3]:https://developer.android.com/design/wear/watchfaces.html
[4]:https://github.com/ustwo/clockwise/wiki/Getting-Started
[5]:https://repository.sonatype.org/service/local/artifact/maven/redirect?r=central-proxy&g=com.ustwo.android&a=clockwise-wearable&p=aar&v=LATEST
[6]:http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22com.ustwo.android%22%20AND%20a%3A%22clockwise-wearable%22
[7]:https://github.com/ustwo/Clockwise/blob/master/RELEASES.md
[8]:https://github.com/ustwo/Clockwise/issues
[9]:mailto:clockwise@ustwo.com
[10]:https://source.android.com/source/code-style.html
[11]:https://github.com/ustwo/clockwise-samples
