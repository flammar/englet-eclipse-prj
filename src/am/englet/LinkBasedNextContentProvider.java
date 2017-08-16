package am.englet;

import am.englet.Links.NextContentProvider;
import am.englet.link.FinalLink;
import am.englet.link.Link;

public class LinkBasedNextContentProvider implements NextContentProvider {

    private static final long serialVersionUID = -113032386104045180L;
    private Link current;

    public LinkBasedNextContentProvider(final Link current) {
        this.current = new FinalLink(null, current);
    }

    public Object tryNextContent() {
        if (current != null)
            current = current.next();
        return (current == null) ? null : Links.nullCorrect(current.content());

    }

}
