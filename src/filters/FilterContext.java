package filters;

import models.Media;
import java.util.List;

public class FilterContext {
    private MediaFilter strategy;

    public void setStrategy(MediaFilter strategy) {
        this.strategy = strategy;
    }

    public List<Media> executeFilter(List<Media> mediaList) {
        if (strategy == null) return mediaList;
        return strategy.filter(mediaList);
    }
}
