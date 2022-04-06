package controller;

import configuration.DateAccessConf;
import model.Core;

public class Controller
{
	private Core core;

	public Controller(DateAccessConf dateAccessConf)
	{
		//делает дела для настройки контроллера

		//Инициализация ядра
		core = new Core(dateAccessConf);
	}
}
