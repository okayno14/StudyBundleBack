package business.bundle.matrix;

public class Matrix
{
	private Row rows[];
	private int width;
	private int height;
	private Row toCompare;

	public Matrix(int width, int height, Row toCompare, Row rows[])
	{
		this.width     = width;
		this.height    = height;
		this.toCompare = toCompare;
		this.rows      = rows;
	}

	private void quickSort(int low, int high, int column)
	{
		if (low >= high) return;

		// Средний элемент
		int middle = low + (high - low) / 2;
		Float opora = rows[middle].getCortege()[column];

		// Деление СД на два подмножества
		int i = low, j = high;
		while (i <= j)
		{
			while(rows[i].getCortege()[column]>opora) i++;

			while(rows[j].getCortege()[column]<opora) j--;

			if(i<=j)
			{
				Row buf = rows[i];
				rows[i]=rows[j];
				rows[j]=buf;
				i++;
				j--;
			}
		}
		// Рекурсивная сортировка левого и правого подмножеств
		if (low < j) quickSort(low, j,column);
		if (high > i) quickSort(i, high,column);
	}

	public void sortDesc(int column)
	{
		if(column>=width || height==0)
		{
			return;
		}
		quickSort(0,height-1,width-1);
	}

	public Matrix getSubmatrix(int height)
	{
		if(height>this.height)
		{
			return null;
		}

		Row rows[] = new Row[height];
		for(int i=0;i<this.rows.length;i++)
		{
			rows[i]=this.rows[i];
		}
		return new Matrix(width,height,toCompare,rows);
	}

	public Row[] getRows()
	{
		return rows;
	}

	public int getWidth()
	{
		return width;
	}

	public int getHeight()
	{
		return height;
	}

	public Row getToCompare()
	{
		return toCompare;
	}
}
