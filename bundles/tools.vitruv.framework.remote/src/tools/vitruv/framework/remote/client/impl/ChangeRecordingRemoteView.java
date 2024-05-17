package tools.vitruv.framework.remote.client.impl;

import java.util.Collection;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;

import tools.vitruv.change.composite.description.TransactionalChange;
import tools.vitruv.change.composite.description.VitruviusChangeResolver;
import tools.vitruv.change.composite.recording.ChangeRecorder;
import tools.vitruv.change.interaction.UserInteractionBase;
import tools.vitruv.framework.remote.client.HasRemoteUuid;
import tools.vitruv.framework.views.CommittableView;
import tools.vitruv.framework.views.ViewSelection;
import tools.vitruv.framework.views.ViewSelector;
import tools.vitruv.framework.views.ViewType;
import tools.vitruv.framework.views.changederivation.StateBasedChangeResolutionStrategy;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;


/**
 * A {@link RemoteView} that records changes to its resources and allows to propagate them
 * back to the vitruv server using the {@link #commitChanges} method.
 */
public class ChangeRecordingRemoteView implements CommittableView, HasRemoteUuid {

    private final RemoteView base;
    private ChangeRecorder changeRecorder;

    public ChangeRecordingRemoteView(RemoteView base) {
        checkArgument(base != null, "base must not be null");
        checkState(!base.isModified(), "view must not be modified");
        this.base = base;
        setupChangeRecorder();
    }

    private void setupChangeRecorder() {
        changeRecorder = new ChangeRecorder(base.viewSource);
        changeRecorder.addToRecording(base.viewSource);
        changeRecorder.beginRecording();
    }

    @Override
    public void close() {
        if (!isClosed()) {
            changeRecorder.close();
        }
        base.close();
    }

    @Override
    public Collection<EObject> getRootObjects() {
        return base.getRootObjects();
    }

    @Override
    public boolean isModified() {
        return base.isModified();
    }

    @Override
    public boolean isOutdated() {
        return base.isOutdated();
    }

    @Override
    public void update() {
        if (changeRecorder.isRecording()) {
            changeRecorder.endRecording();
        }
        changeRecorder.close();
        base.update();
        setupChangeRecorder();
    }

    @Override
    public boolean isClosed() {
        return base.isClosed();
    }

    @Override
    public void registerRoot(EObject object, URI persistAt) {
        base.registerRoot(object, persistAt);
    }

    @Override
    public void moveRoot(EObject object, URI newLocation) {
        base.moveRoot(object, newLocation);
    }

    @Override
    public ViewSelection getSelection() {
        return base.getSelection();
    }

    @Override
    public ViewType<? extends ViewSelector> getViewType() {
        return base.getViewType();
    }


    @Override
    public CommittableView withChangeRecordingTrait() {
        changeRecorder.close();
        return base.withChangeDerivingTrait();
    }

    @Override
    public CommittableView withChangeDerivingTrait(StateBasedChangeResolutionStrategy changeResolutionStrategy) {
        changeRecorder.close();
        return base.withChangeDerivingTrait(changeResolutionStrategy);
    }

    /**
     * Commits the changes made to the view and its containing elements.
     *
     * @throws IllegalStateException if called on a closed view
     * @see #isClosed()
     * @see #commitChangesAndUpdate()
     */
    @Override
    public void commitChanges() {
        base.checkNotClosed();
        var recordedChange = changeRecorder.endRecording();
        var changeResolver = VitruviusChangeResolver.forHierarchicalIds(base.viewSource);
        var unresolvedChanges = changeResolver.assignIds(recordedChange);
        base.remoteConnection.propagateChanges(base.uuid, unresolvedChanges);
        base.modified = false;
        changeRecorder.beginRecording();
    }
    
    public void commitChanges(Iterable<UserInteractionBase> userInputs) {
    	 base.checkNotClosed();
         var recordedChange = changeRecorder.endRecording();
         var changeResolver = VitruviusChangeResolver.forHierarchicalIds(base.viewSource);
         var unresolvedChanges = changeResolver.assignIds(recordedChange);
         ((TransactionalChange<?>) unresolvedChanges).setUserInteractions(userInputs);
         base.remoteConnection.propagateChanges(base.uuid, unresolvedChanges);
         base.modified = false;
         changeRecorder.beginRecording();
    }
    
	public String getRemoteUuid() {
		return base.getRemoteUuid();
	}
}
