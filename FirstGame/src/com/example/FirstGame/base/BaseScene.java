package com.example.FirstGame.base;

import android.app.Activity;
import com.example.FirstGame.manager.ResourcesManager;
import com.example.FirstGame.manager.SceneManager;
import org.andengine.engine.Engine;
import org.andengine.engine.camera.Camera;
import org.andengine.entity.scene.Scene;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

/**
 * User: trungpt
 * Date: 3/1/14
 * Time: 9:01 AM
 */
public abstract class BaseScene extends Scene
{
    public Engine engine;
    public Activity activity;
    public ResourcesManager resourcesManager;
    public VertexBufferObjectManager vbom;
    public Camera camera;

    public BaseScene()
    {
        this.resourcesManager = ResourcesManager.getInstance();
        this.engine = resourcesManager.engine;
        this.activity = resourcesManager.activity;
        this.vbom = resourcesManager.vbom;
        this.camera = resourcesManager.camera;
        createScene();
    }

    public abstract void createScene();

    public abstract void onBackKeyPressed();

    public abstract SceneManager.SceneType getSceneType();

    public abstract void disposeScene();
}
