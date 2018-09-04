# frame-progress-bar

## Introduction
Library based on [android-square-progressbar](https://github.com/mrwonderman/android-square-progressbar). Added ability to nest any view  progress bar and few other features like changing direction of progress bar or setting starting place.

## Usage
### Gradle
 The Gradle dependency is available via jCenter. jCenter is the default Maven repository used by Android Studio.
 
 Add to your build.gradle
 
    dependencies {
        //Your other dependencies here
        compile 'pl.owsica:frame-progress-bar:0.1.2'
    }

![Lib Sample](http://i.imgur.com/SFXUnxg.png)

### Code

FrameProgressBar is ViewGroup so you can are able to nest views insiade. To note - FrameProgressBar accepts only one child so if you want to display more views inside you will have to wrap it in another ViewGroup.    

    <pl.owsica.andrzej.frameprogressbar.FrameProgressBar
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        app:frame_thickness="15dp"
        app:progress_color="@color/green_600">
        
        <ImageView
            android:layout_width="180dp"
            android:layout_height="180dp"
            android:src="@drawable/sample" />
            
    </pl.owsica.andrzej.frameprogressbar.FrameProgressBar>
    
FrameProgressBar has also indeterminate work mode, you will only have to pass it in layout file

    app:indeterminate="true"
    
### Customization

You can easily customize progress bar apperance. Here are some editable attributes:

* **frame_thicknes**
* **progress_color** - color of progress bar(moving one)
* **background_color** - color of background frame(it's transparent by default)
* **clockwise** - accepts true or false, defines direction of progress bar
* **start_place** - accepts left/right/top/bottom defines starting ledge of progress bar
* **indeterminate** - true/false - self explenatory
* **indeterminate_length** - dimension - lenght of indeterminate bar
* **indeterminate_speed** - numeric - speed of indeterminate rectangle

You can change every attribute in java code after instantiating of the view. More code examples could be find in sample app or on Wiki.

