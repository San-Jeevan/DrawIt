package com.jeevan.drawIt;

import java.util.ArrayList;
import java.util.List;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

public class DrawView extends View {
	List<Point> points = new CustomArrayList<Point>();
	Paint paint = new Paint();
	Paint paint2 = new Paint();
	Path path = new Path();

	
	public class CustomArrayList<Point> extends ArrayList<Point> {

		@Override
		public boolean add(Point object) {
			CustomInvalidate();
			return super.add(object);
		}
	}


	public void CustomInvalidate () {
		invalidate();
	}

	public DrawView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setFocusable(true);
		setFocusableInTouchMode(true);
		paint.setColor(Color.BLACK);
		paint2.setColor(Color.BLACK);
		paint.setStrokeWidth(5);
		paint.setStyle(Paint.Style.STROKE);
	
	}

	public void addpoint(Point p) {
		points.add(p);
	}

	@Override
	public void onDraw(Canvas canvas) {
		path = new Path();
		canvas.drawColor(Color.WHITE);

		try {
			for (Point point : points) {
				if (point.state == 0) {
					path.moveTo(point.x, point.y);
					canvas.drawCircle(point.x, point.y, 3, paint2);
				}
				else if (point.state == 1) {
					path.moveTo(point.x, point.y);
					canvas.drawCircle(point.x, point.y, 3, paint2);
				}
				else 
				path.lineTo(point.x, point.y);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		canvas.drawPath(path, paint);
	}

	

	

}

