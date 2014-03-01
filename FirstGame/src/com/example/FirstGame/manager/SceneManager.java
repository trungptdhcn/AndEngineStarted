package com.example.FirstGame.manager;

import com.example.FirstGame.base.BaseScene;
import org.andengine.engine.Engine;

/**
 * User: trungpt
 * Date: 3/1/14
 * Time: 9:07 AM
 */
public class SceneManager
{
    private BaseScene splashScene;
    private BaseScene menuScene;
    private BaseScene gameScene;
    private BaseScene loadingScene;

    public static final SceneManager INSTANCE = new SceneManager();
    private SceneType currentSceneType = SceneType.SCENE_SPLASH;

    private BaseScene currentScene;
    private Engine engine = ResourcesManager.getInstance().engine;

    public enum SceneType
    {
        SCENE_SPLASH,
        SCENE_MENU,
        SCENE_GAME,
        SCENE_LOADING,
    }

    public void setScene(BaseScene scene)
    {
        engine.setScene(scene);
        currentScene = scene;
        currentSceneType = scene.getSceneType();
    }

    public void setScene(SceneType sceneType)
    {
        switch (sceneType)
        {
            case SCENE_MENU:
                setScene(menuScene);
                break;
            case SCENE_GAME:
                setScene(gameScene);
                break;
            case SCENE_SPLASH:
                setScene(splashScene);
                break;
            case SCENE_LOADING:
                setScene(loadingScene);
                break;
            default:
                break;
        }
    }

    public static SceneManager  getInstance()
    {
        return INSTANCE;
    }
    public SceneType getCurrentSceneType()
    {
        return currentSceneType;
    }
    public BaseScene getCurrentScene()
    {
        return currentScene;
    }
}
