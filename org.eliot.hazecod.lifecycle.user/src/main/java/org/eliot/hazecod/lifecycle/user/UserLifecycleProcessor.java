package org.eliot.hazecod.lifecycle.user;

import org.eliot.hazecod.management.user.User;

public interface UserLifecycleProcessor {

    LifecycleResult process(User user);

}
