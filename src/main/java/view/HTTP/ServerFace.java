package view.HTTP;

import configuration.DateAccessConf;
import configuration.HTTP_Conf;
import controller.Controller;

public class ServerFace
{
	private HTTP_Conf http_conf;
	private Controller controller;

	public ServerFace(HTTP_Conf http_conf, DateAccessConf dateAccessConf)
	{
		this.http_conf = http_conf;

		//применяем параметры конфигурации

		//строим контроллер

		controller = new Controller(dateAccessConf);
		//стартуем сервер
	}
}
