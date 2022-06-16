package configuration;

public class BusinessConfiguration
{
	private final long  reservedRoleId[]           = {9L, 10L, 11L, 12L};
	private final int   META_ANALYSIS_WINDOW       = 5;
	private final float WORD_ANALYSIS_CRITICAL_VAL = 0.75f;
	private final int   TOKEN_LENGTH               = 10;
	//timers
	private final long  AUTHENTICATION_TIME_MS     = 1L * 60L * 1000L;
	private final long  AUTH_TIMER_CLOCK_MS        = AUTHENTICATION_TIME_MS / 5;
	private final long  SESSION_TIMEOUT_MS         = AUTHENTICATION_TIME_MS + 10000L;
	private final long  CACHE_CLEARING_COOLDOWN_MS = 2L * 60L * 1000L;

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

	public long getAUTHENTICATION_TIME_MS()
	{
		return AUTHENTICATION_TIME_MS;
	}

	public long getAUTH_TIMER_CLOCK_MS()
	{
		return AUTH_TIMER_CLOCK_MS;
	}

	public long getSESSION_TIMEOUT_MS()
	{
		return SESSION_TIMEOUT_MS;
	}

	public long getCACHE_CLEARING_COOLDOWN_MS()
	{
		return CACHE_CLEARING_COOLDOWN_MS;
	}
}