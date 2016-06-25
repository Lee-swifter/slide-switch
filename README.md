# slide-switch
A switch view for Android, it looks better than the official, support both slide and click.

Inspired by [Leaking/SlideSwitch](https://github.com/Leaking/SlideSwitch), the rep is great but there are something which are unsuitable for me, so I modifid some code and make this rep. 

###Attributes:
```xml
<declare-styleable name="slide_switch">
        <attr name="front_color" format="color" />
        <attr name="back_color" format="color" />
        <attr name="switch_color" format="color" />
        <attr name="state" format="boolean" />
        <attr name="shape">
            <enum name="rect" value="1" />
            <enum name="circle" value="2" />
        </attr>
        <attr name="min_width" format="dimension" />
        <attr name="min_height" format="dimension" />
        <attr name="boundary_distance" format="dimension" />
        <attr name="open_direction" >
            <enum name="right" value="1" />
            <enum name="left" value="2" />
        </attr>
        <attr name="slideable" format="boolean" />
    </declare-styleable>
```
