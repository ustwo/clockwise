<img src="https://github.com/ustwo/clockwise/wiki/images/ustwo_clockwise_framework_promo.jpg" width="100%" />

Clockwise is a watch face framework for Android Wear developed by ustwo. It extends the [Android Wear Watch Face API][1] and provides base classes and helpers for quickly and correctly developing watch faces. This includes properly handling the various modes of operation, hardware constraints, changes in date/time/time zone, access to data, and performance considerations.  

### Background  

ustwo worked with Google to develop the [first watch faces on the Android Wear platform][2], and in doing so, we learned a great deal and identified the benefit of extending the existing watch face API into an open source framework. The purpose of Clockwise is to help developers more easily consider the inherent nuances in developing watch faces on the Android Wear platform, including varying hardware specifications and battery life conservation. The goal is that by utilizing the Clockwise development framework in conjunction with the [Watch Face design guidelines][3] (also created by ustwo), developers can enhance the user's experience on Android Wear.

For more details, full documentation, and code samples, please see the [Clockwise Wiki][4].

### Integrate Clockwise  

Once you have your environment setup to start developing watch faces, you can then use one of the following methods to integrate the Clockwise framework into your project:

#### Source Code  

Clone repository =>  

```
git clone https://github.com/ustwo/Clockwise.git
```  

#### Wearable Library  

Download Latest AAR => [clockwise-wearable.aar][5]  

Gradle =>  
```groovy
compile 'com.ustwo.android:clockwise-wearable:x.y.z'
```
Maven =>
```xml
<dependency>
  <groupId>com.ustwo.android</groupId>
  <artifactId>clockwise-wearable</artifactId>
  <version>x.y.z</version>
</dependency>
```
Binary History => [Maven Central Repository][6]

#### Mobile Library  

_Coming Soon…_  

## Releases

Release Notes => [RELEASES.md][7]  
Binary History => [Maven Central Repository][6]

## About ![ustwo](https://media.licdn.com/media/p/4/005/02e/351/2f4017d.png)

ustwo developed the first watch faces on the Android Wear platform, and in doing so, identified the benefit of extending the existing watch face API into an open source framework.

ustwo plans to start accepting pull requests for this repo in the near future. In the meantime, please continue to utilize [GitHub Issues][8] and email us at [clockwise@ustwo.com][9] with any and all feedback. We would love to hear your thoughts on how we can improve Clockwise!  

## License

     The MIT License (MIT)  
      
     Copyright (c) 2015 ustwo studio inc ([www.ustwo.com])  
      
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
[4]:https://github.com/ustwo/Clockwise/wiki/
[5]:https://repo1.maven.org/maven2/com/ustwo/android/clockwise-wearable/1.0.0/clockwise-wearable-1.0.0.aar
[6]:http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22com.ustwo.android%22%20AND%20a%3A%22clockwise-wearable%22
[7]:https://github.com/ustwo/Clockwise/blob/master/RELEASES.md
[8]:https://github.com/ustwo/Clockwise/issues
[9]:mailto:clockwise@ustwo.com
