package commons;

public class ErrorCodes {
	public static final int INVALID_EVENT_ID = 800,
			                INVALID_USER_EMAIL = 900,
			                UNAUTHORIZED_USER = 901,
			                VOID_CLASS_FIELD = 950,
					        GENERIC_SQL = 951;
	
	public static final String INVALID_EVENT_ID_DESCRIPTION = "Invalid Event Id",
							  INVALID_USER_EMAIL_DESCRIPTION = "Invalid User Email",
							  UNAUTHORIZED_USER_DESCRIPTION = "Unauthorized User",
				              VOID_CLASS_FIELD_EXC_DESCRIPTION = "Void Class Field",
						      GENERIC_SQL_EXC_DESCRIPTION = "Generic SQL";
}
