package business.bundle;

import dataAccess.entity.Bundle;

public interface BundlePredicate
{
	boolean check(Bundle sample, Bundle b);
}
