package configuration;

public class BusinessConfiguration
{
	private final long  reservedRoleId[]           = {9L, 10L, 11L, 12L};
	private final int   META_ANALYSIS_WINDOW       = 5;
	private final float WORD_ANALYSIS_CRITICAL_VAL = 0.75f;
	private final int   TOKEN_LENGTH               = 10;
	private final long  AUTHENTICATION_TIME        = 1L * 60 * 1000;

	public BusinessConfiguration()
	{
	}

	public long[] getReservedRoleId()
	{
		return reservedRoleId;
	}

	public int getMETA_ANALYSIS_WINDOW()
	{
		return META_ANALYSIS_WINDOW;
	}

	public float getWORD_ANALYSIS_CRITICAL_VAL()
	{
		return WORD_ANALYSIS_CRITICAL_VAL;
	}

	public int getTOKEN_LENGTH()
	{
		return TOKEN_LENGTH;
	}

	public long getAUTHENTICATION_TIME()
	{
		return AUTHENTICATION_TIME;
	}
}