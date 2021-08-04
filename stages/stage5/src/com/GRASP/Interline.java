package com.GRASP;

import java.lang.Math;

class Interline implements Highlightable {
    public float height;
    /*@Nullable*/ public Line following_line = null;
    
    public float onward_height() {
	return height
	    + ((following_line == null)
	       ? 0
	       : following_line.onward_height());
    }

    public float minimum_height() {
	return 
	    ((following_line == null)
	     ? 0
	     : following_line.minimum_height());
    }
    
    public float maximum_width() {
	if (following_line == null) {
	    return 0;
	}
	float line_width = following_line.width();
	float remaining_width =
	    (following_line.next_interline == null)
	    ? 0
	    : following_line.next_interline.maximum_width();
	if (line_width > remaining_width) {
	    return line_width;
	}
	return remaining_width;
    }

    public float minimum_width() {
	if (following_line == null) {
	    return 0;
	}
	float line_width = following_line.minimum_width();
	float remaining_width =
	    (following_line.next_interline == null)
	    ? 0
	    : following_line.next_interline.maximum_width();
	if (line_width > remaining_width) {
	    return line_width;
	}
	return remaining_width;
    }

    
    public Interline(float h, Line line) {
	//GRASP.log("interline h="+h);
	height = Math.max(0, h);
	following_line = line;
    }

    public Interline(float h) {
	this(h, (Line) null);
    }

    public Interline remove_following_line() {

	if (following_line != null) {
	    height += following_line.height();
	    Interline next_interline = following_line.next_interline;
	    if (next_interline != null) {
		height += next_interline.height;
		following_line = next_interline.following_line;
	    }
	    else {
		following_line = null;
	    }

	}
	return this;
    }


    public Line remove_empty_lines() {
	while (following_line != null
	       && following_line.isEmpty()) {
	    remove_following_line();
	}
	return following_line;
    }

    private float highlighted = Float.NaN;
    
    @Override
    public boolean is_highlighted() {
	return !Float.isNaN(highlighted);
    }

    @Override
    public void highlight(float x, float y) {
	highlighted = x;
    }

    @Override
    public void unhighlight() {
	highlighted = Float.NaN;
    }

    public Interline deep_copy() {
	return new Interline(height,
			     ((following_line == null)
				 ? null
				 : following_line.deep_copy()));
    }

    public boolean insert_line_with(DragAround dragged,
				    float x, float y) {
	float h = dragged.height();
	Interline interline = new
	    Interline(Math.max(0, height - h),
		      following_line);
	following_line = new Line(new Space(dragged.x,
					    dragged.target));
	height = Math.max(0, height - h - interline.height); 
	following_line.next_interline = interline;
	return true;
    }
    
}
