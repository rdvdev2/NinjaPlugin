package tk.rdvdev2.ninjaplugin.ninja.declarations

import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input

class CommentDeclaration implements NinjaDeclaration {
    @Input final Property<String> comment
    @Input final Property<Boolean> prefixSpace

    CommentDeclaration(ObjectFactory objects, String comment, boolean prefixSpace) {
        this.comment = objects.property(String)
        this.comment.set(comment)

        this.prefixSpace = objects.property(Boolean)
        this.prefixSpace.set(prefixSpace)
    }

    CommentDeclaration(ObjectFactory objects, String comment) {
        this(objects, comment, true)
    }

    void setComment(String value) {
        comment.set(value)
    }

    void setPrefixSpace(boolean value) {
        prefixSpace.set(value)
    }

    @Override
    List<String> parse() {
        List<String> ret = new ArrayList<>(1)
        ret.add("#"+(prefixSpace.get()?" ":"")+comment.get())
        return ret
    }
}
