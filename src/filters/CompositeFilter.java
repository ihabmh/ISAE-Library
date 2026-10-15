package filters;

import models.Media;
import java.util.ArrayList;
import java.util.List;

public class CompositeFilter implements MediaFilter {
    private List<MediaFilter> filters = new ArrayList<>();

    public void addFilter(MediaFilter filter) {
        filters.add(filter);
    }

    @Override
    public List<Media> filter(List<Media> mediaList) {
        List<Media> result = mediaList;
        for (MediaFilter filter : filters) {
            result = filter.filter(result);
        }
        return result;
    }
}
