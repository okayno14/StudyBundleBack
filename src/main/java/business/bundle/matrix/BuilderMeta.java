package business.bundle.matrix;

import dataAccess.entity.Bundle;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class BuilderMeta implements BuilderMatrix
{
	List<Bundle> bundles;
	Bundle       toCompare;
	final int width = 4;
	int height;

	public BuilderMeta(Bundle toCompare, List<Bundle> bundles)
	{
		this.toCompare = toCompare;
		this.toCompare.getReport().setSetCompatible(false);
		this.bundles = bundles;
		height       = bundles.size();
	}

	private Row buildRow(Bundle b)
	{
		Float cortege[] = new Float[width];
		cortege[0] = (float) b.getReport().getSymCount();
		cortege[1] = (float) b.getReport().getSymCountNoSpace();
		cortege[2] = (float) b.getReport().getUniqueWords();
		cortege[3] = (float) b.getReport().getWordCount();
		return new Row(b, cortege);
	}

	@Override
	public Matrix buildMatrix()
	{
		Row rows[] = new Row[height];
		int i      = 0;
		for (Iterator<Bundle> iterator = bundles.iterator(); iterator.hasNext(); )
		{
			rows[i++] = buildRow(iterator.next());
		}
		return new Matrix(width, height, buildRow(toCompare), rows);
	}
}
