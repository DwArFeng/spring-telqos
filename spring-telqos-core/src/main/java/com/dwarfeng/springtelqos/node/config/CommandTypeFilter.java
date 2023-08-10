package com.dwarfeng.springtelqos.node.config;

import com.dwarfeng.springtelqos.stack.command.Command;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.core.type.filter.TypeFilter;

import javax.annotation.Nonnull;
import java.io.IOException;

/**
 * 命令类型过滤器。
 *
 * @author DwArFeng
 * @since 1.1.7
 */
final class CommandTypeFilter implements TypeFilter {

    public static final CommandTypeFilter INSTANCE = new CommandTypeFilter();

    private final AnnotationTypeFilter annotationTypeFilter = new AnnotationTypeFilter(TelqosCommand.class);
    private final AssignableTypeFilter assignableTypeFilter = new AssignableTypeFilter(Command.class);

    @Override
    public boolean match(@Nonnull MetadataReader metadataReader, @Nonnull MetadataReaderFactory metadataReaderFactory)
            throws IOException {
        return annotationTypeFilter.match(metadataReader, metadataReaderFactory) &&
                assignableTypeFilter.match(metadataReader, metadataReaderFactory);
    }
}
