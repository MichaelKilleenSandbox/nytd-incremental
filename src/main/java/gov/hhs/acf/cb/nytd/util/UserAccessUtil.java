package gov.hhs.acf.cb.nytd.util;

import gov.hhs.acf.cb.nytd.models.SiteUser;
import gov.hhs.acf.cb.nytd.service.TransmissionServiceP3;
import org.apache.commons.lang3.StringUtils;


import javax.servlet.http.HttpServletRequest;
import java.util.Map;

public class UserAccessUtil {


    public static boolean hasAccessToGenerateLetter(TransmissionServiceP3 transmissionService,
                                                    HttpServletRequest request, Map<String, Object> session) {
        SiteUser user = (SiteUser) session.get(Constants.SITE_USER);
        String transmissionType = transmissionService.getTransmissionType(
                request.getParameter(Constants.TRANSMISSION_QUERY_PARAM));

        return (user.isSecondaryStateRoleManager()
                    || user.isSecondaryStateRoleStateAuthorized()
                    || user.hasPrivilege(Constants.PRIV_CAN_ADMIN_ALL_USERS))
                &&
                    (StringUtils.equals(transmissionType, Constants.REGULAR_TRANSMISSION)
                    || StringUtils.equals(transmissionType, Constants.CORRECTED_TRANSMISSION));
    }

    public static boolean hasAccessToInitialExport(TransmissionServiceP3 transmissionService,
                                                   HttpServletRequest request, Map<String, Object> session) {
        SiteUser user = (SiteUser) session.get(Constants.SITE_USER);
        String transmissionType = transmissionService.getTransmissionType(
                request.getParameter(Constants.TRANSMISSION_QUERY_PARAM));

        return user.hasPrivilege(Constants.PRIV_CAN_ADMIN_ALL_USERS)
                &&
                StringUtils.equals(transmissionType, Constants.REGULAR_TRANSMISSION);
    }


    public static boolean hasAccessToFinalExport(TransmissionServiceP3 transmissionService,
                                            HttpServletRequest request, Map<String, Object> session) {
        SiteUser user = (SiteUser) session.get(Constants.SITE_USER);

        return user.hasPrivilege(Constants.PRIV_CAN_ADMIN_ALL_USERS);
    }
}
