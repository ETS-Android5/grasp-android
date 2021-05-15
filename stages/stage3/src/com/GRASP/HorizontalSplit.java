package com.GRASP;
import android.graphics.Canvas;
import android.graphics.RectF;

class HorizontalSplit extends Split {

    public HorizontalSplit(float x, float y, float w, float h,
			   Panel left_panel,
			   Panel right_panel) {
	super(x, y, w, h, left_panel, right_panel);
    }
    
    public HorizontalSplit(Panel panel,
			   RectF rect) {
	super(panel.left(), panel.top(),
	      panel.width(), panel.height(),
	      panel, panel.copy());
	float center = (rect.left + rect.right)/2.0f;
	
	firstPanel.setWidth(center - left() - bar_width/2.0f);
	secondPanel.setLeft(center + bar_width/2.0f);
	secondPanel.setWidth(width() - firstPanel.width()
			     - bar_width);

	secondPanel.scrollBy(-(firstPanel.width()
			       + bar_width), 0);

	/*
	GRASP.log("HorizontalSplit("+(int)left+", "
		  +(int)top+", "
		  +(int)width+", "
		  +(int)height+")@"
		  +(int)center);*/
    }

    @Override
    public Panel copy() {
	return new HorizontalSplit(left(), top(),
				   width(), height(),
				   firstPanel, secondPanel);
    }
    
    @Override
    public void render(Canvas canvas) {
	canvas.save();
	canvas.drawRect(firstPanel.width(),
			0,
			firstPanel.width() + bar_width,
			firstPanel.height(),
			GRASP.paint);
	
	canvas.clipRect(0, 0,
			firstPanel.width(),
			firstPanel.height());
	firstPanel.render(canvas);
	canvas.restore();
	
	canvas.save();
	canvas.translate((firstPanel.width() + bar_width), 0);
	canvas.clipRect(0, 0,
			secondPanel.width(),
			secondPanel.height());
	secondPanel.render(canvas);
	canvas.restore();
    }

    @Override
    public String toString() {
	return "HS("+firstPanel.toString()
	    +", "+secondPanel.toString()+")";
    }
    
    @Override
    public Drag onPress(Screen screen,
			int finger,
			float x, float y) {
	if (firstPanel.right() < x && x < secondPanel.left()) {
	    return translate(super
			     .onPress(screen, finger,
				      x-firstPanel.right(), y),
			     firstPanel.right(), 0);
	}
	if (x <= firstPanel.right()) {
	    return firstPanel.onPress(screen, finger, x, y);
	}
	if (x >= secondPanel.left()) {
	    return translate(secondPanel
			     .onPress(screen, finger,
				      x-secondPanel.left(), y),
			     secondPanel.left(), 0);
	}
	assert(false);
	return null;
    }

    @Override
    public void onClick(Screen screen,
			int finger,
			float x, float y) {
	if (firstPanel.right() < x && x < secondPanel.left()) {
	    super.onClick(screen, finger, x-firstPanel.right(), y);
	    return;
	}
	if (x <= firstPanel.right()) {
	    firstPanel.onClick(screen, finger, x, y);
	    return;
	}
	if (x >= secondPanel.left()) {
	    secondPanel.onClick(screen, finger,
				x-secondPanel.left(), y);
	    return;
	}
	assert(false);
    }


    @Override
    public Drag onSecondPress(Screen screen,
			      int finger,
			      float x, float y) {
	if (firstPanel.right() < x && x < secondPanel.left()) {
	    return translate(super
			     .onSecondPress(screen, finger,
				      x-firstPanel.right(), y),
			     firstPanel.right(), 0);
	}
	if (x <= firstPanel.right()) {
	    return firstPanel.onSecondPress(screen, finger, x, y);
	}
	if (x >= secondPanel.left()) {
	    return translate(secondPanel
			     .onSecondPress(screen, finger,
					    x-secondPanel.left(), y),
			     secondPanel.left(), 0);
	}
	assert(false);
	return null;
    }

    @Override
    public void onDoubleClick(Screen screen,
			      int finger,
			      float x, float y) {
	if (firstPanel.right() < x && x < secondPanel.left()) {
	    super.onDoubleClick(screen, finger,
				x-firstPanel.right(), y);
	    return;
	}
	if (x <= firstPanel.right()) {
	    firstPanel.onDoubleClick(screen, finger, x, y);
	    return;
	}
	if (x >= secondPanel.left()) {
	    secondPanel.onDoubleClick(screen, finger,
				      x-secondPanel.left(), y);
	    return;
	}
	assert(false);
    }

    @Override
    public Drag onHold(Screen screen,
		       int finger,
		       float x, float y) {
	if (firstPanel.right() < x && x < secondPanel.left()) {
	    return translate(super
			     .onHold(screen, finger,
				     x-firstPanel.right(), y),
			     firstPanel.right(), 0);
	}
	if (x <= firstPanel.right()) {
	    return firstPanel.onHold(screen, finger, x, y);
	}
	if (x >= secondPanel.left()) {
	    return translate(secondPanel
			     .onHold(screen, finger,
					    x-secondPanel.left(), y),
			     secondPanel.left(), 0);
	}
	assert(false);
	return null;	
    }
    
    @Override
    public Panel
	finishResizing(Split s, float vx, float vy) {
	
	if (s == this) {
	    if (vx > closing_threshold
		|| secondPanel.width() <= bar_width) {
		firstPanel.setWidth(width());
		firstPanel.setLeft(left());
		return firstPanel;
	    }
	
	    if (vx < -closing_threshold
		|| firstPanel.width() <= bar_width) {
		secondPanel.setWidth(width());
		secondPanel.setLeft(left());
		return secondPanel;
	    }
	}

	assert(firstPanel.right() < secondPanel.left());
	
	if (s.right() <= firstPanel.right()) {
	    firstPanel = firstPanel.finishResizing(s, vx, vy);
	}
	else if (s.left() >= secondPanel.left()) {
	    secondPanel =
		secondPanel.finishResizing(s, vx, vy);
	}
	return this;
    }

    @Override
    public void resizeBy(float dx, float dy) {
	firstPanel.setWidth(firstPanel.width() + dx);
	secondPanel.setLeft(secondPanel.left() + dx);
	secondPanel.setWidth(secondPanel.width() - dx);
    }

    @Override
    public void setTop(float v) {
	super.setTop(v);
	firstPanel.setTop(v);
	secondPanel.setTop(v);
    }

    @Override
    public void setLeft(float v) {
	super.setLeft(v);
	firstPanel.setLeft(v);
	secondPanel.setLeft(v + firstPanel.width()
			   + bar_width);
    }

    @Override
    public void setHeight(float v) {
	super.setHeight(v);
	firstPanel.setHeight(v);
	secondPanel.setHeight(v);
    }

    @Override
    public void setWidth(float w0_) {
	float w0 = width();
	float w1 = firstPanel.width();
	float w2 = secondPanel.width();
	assert(w0 == w1 + w2 + bar_width);
	float b2 = bar_width/2.0f;

	float w1_ = w0_*(w1+b2)/w0 - b2;
	
	float w2_ = w0_ - w1_ - bar_width;
	
	super.setWidth(w0_);
	firstPanel.setWidth(w1_);
	secondPanel.setLeft(left() + firstPanel.width()
			   + bar_width);

	secondPanel.setWidth(w2_);
    }
    
}
