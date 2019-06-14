package com.good.automated.test.screenFinder.checker;

import com.good.automated.test.screenFinder.view.BBDView;

public class ViewChecker {

    private String packageName;
    private String resourceId;
    private IViewListener listener;

    public ViewChecker(String packageName, String resourceId, IViewListener listener) {
        this.packageName = packageName;
        this.resourceId = resourceId;
        this.listener = listener;
    }

    public boolean compareAndNotify(BBDView view) {
        boolean viewFound = false;

        viewFound = view.getPackageName() != null && view.getPackageName().equals(packageName);

        if (viewFound) {
            viewFound = view.getResourceId() != null && view.getResourceId().equals(resourceId);
        }

        if (viewFound)
            listener.onViewFound();

        return viewFound;
    }

}
