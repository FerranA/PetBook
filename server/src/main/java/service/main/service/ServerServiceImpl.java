package service.main.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import service.main.entity.*;
import service.main.entity.output.*;
import service.main.exception.AlreadyExistsException;
import service.main.exception.BadRequestException;
import service.main.exception.NotFoundException;
import service.main.repositories.EventoRepository;
import service.main.repositories.MascotaRepository;
import service.main.repositories.UserRepository;
import service.main.util.EmailServiceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service("serverService")
public class ServerServiceImpl implements ServerService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EventoRepository eventoRepository;

    @Autowired
    private MascotaRepository mascotaRepository;

    public OutLogin ConfirmLogin(String email, String password) throws NotFoundException {
        Optional<User> user = userRepository.findById(email);
        if (!user.isPresent()) throw new NotFoundException("The user does not exist in the database.");
        boolean result = user.get().checkPassword(password);
        return new OutLogin(result,user.get().isMailconfirmed());
    }

    public void RegisterUser(User input) {
        //TODO check if input has email and password
        userRepository.save(input);
    }
    public void ConfirmEmail(String email) throws NotFoundException {
        Optional<User> user = userRepository.findById(email);
        if (!user.isPresent()) throw new NotFoundException("The user does not exist in the database.");
        user.get().setMailconfirmed(true);
        userRepository.save(user.get());
    }

    public void SendConfirmationEmail(String email) throws NotFoundException, BadRequestException {
        Optional<User> user = userRepository.findById(email);
        if (!user.isPresent()) throw new NotFoundException("The user does not exist in the database.");
        if (user.get().isMailconfirmed()) throw new BadRequestException("The user has already verified his email.");
        sendEmail();
    }

    private void sendEmail() {
        EmailServiceImpl emailService = new EmailServiceImpl();
        emailService.sendSimpleMessage("fewrna@gmail.com", "yea", "Just testing my friend");
    }



    public User getUserByEmail(String email) throws NotFoundException {
        Optional<User> userToReturn = userRepository.findById(email);
        if (!userToReturn.isPresent()) throw new NotFoundException("There is no user with that email");
        else return userToReturn.get();
    }


    public void updateUserByEmail(String email, OutUpdateUserProfile userUpdated) throws NotFoundException {
        Optional<User> userToUpdate = userRepository.findById(email);
        if (!userToUpdate.isPresent()) throw new NotFoundException("There is no user with that email");
        else {
            User user = userToUpdate.get();
            userRepository.delete(user);
            user.setFirstName(userUpdated.getFirstName());
            user.setSecondName(userUpdated.getSecondName());
            user.setDateOfBirth(userUpdated.getDateOfBirth());
            user.setPostalCode(userUpdated.getPostalCode());
            userRepository.insert(user);
        }
    }


//

    public void creaEvento(String userEmail, Integer any, Integer mes, Integer dia, Integer hora, Integer coordenadas, Integer radio) throws AlreadyExistsException, NotFoundException {
        Fecha fecha2 = new Fecha(any, mes, dia ,hora);
        Localizacion localizacion = new Localizacion(coordenadas,radio);

        Evento evento = new Evento(userEmail, localizacion.getId(), fecha2.getId());
        if(eventoRepository.existsById(evento.getId())) throw new AlreadyExistsException("El Evento ya existe en el sistema");
        eventoRepository.save(evento);
    }



    public List<Evento> findAllEventos() {
        return eventoRepository.findAll();
    }


    public void updateEvento(String email, DataEventoUpdate evento) throws NotFoundException {
        Fecha fecha2 = new Fecha(evento.getAny(), evento.getMes(), evento.getDia() ,evento.getHora());
        Localizacion localizacion = new Localizacion(evento.getCoordenadas(),evento.getRadio());

        Evento evento2 = new Evento(email, localizacion.getId(), fecha2.getId(), evento.getDescripcion(), evento.getPublico(), evento.getNumero_asistentes(), evento.getParticipantes());
        if(! eventoRepository.existsById(evento2.getId())) throw new NotFoundException("El Evento no existe en el sistema");
        eventoRepository.deleteById(evento2.getId());
        eventoRepository.insert(evento2);
    }


    public void deleteEvento(String userEmail, Integer any, Integer mes, Integer dia, Integer hora, Integer coordenadas, Integer radio) throws NotFoundException {
        Fecha fecha2 = new Fecha(any, mes, dia ,hora);
        Localizacion localizacion = new Localizacion(coordenadas,radio);

        Evento evento = new Evento(userEmail, localizacion.getId(), fecha2.getId());
        if(!eventoRepository.existsById(evento.getId())) throw new NotFoundException("El Evento no existe en el sistema");
        eventoRepository.deleteById(evento.getId());
    }


    public void creaMascota(String email, String nom_mascota) throws AlreadyExistsException, NotFoundException {
        Mascota mascota = new Mascota(nom_mascota,email);
        if(! userRepository.existsById(email)) throw new NotFoundException("El Usuario no existe en el sistema");
        if(mascotaRepository.existsById(mascota.getId())) throw new AlreadyExistsException("La mascota ya existe en el sistema");
        mascotaRepository.save(mascota);
    }


    public Optional<Mascota> mascota_findById(String emailDuenyo, String nombreMascota) throws NotFoundException {
        String id = nombreMascota+emailDuenyo;
        if(! userRepository.existsById(emailDuenyo)) throw new NotFoundException("El Usuario no existe en el sistema");
        if(! mascotaRepository.existsById(id)) throw new NotFoundException("La Mascota no existe en el sistema");
        return mascotaRepository.findById(id);
    }

    public void updateMascota(String email, DataMascotaUpdate mascota) throws NotFoundException {
        Mascota mascota2 = new Mascota(mascota.getNombre(), email, mascota.getEspecie(), mascota.getRaza(),
                                       mascota.getSexo(), mascota.getDescripcion(), mascota.getFoto());

        String id = mascota2.getId();
        if(! mascotaRepository.existsById(id)) throw new NotFoundException("La Mascota no existe en el sistema");
        mascotaRepository.deleteById(id);
        mascotaRepository.save(mascota2);
    }

    public List<Mascota> findAllMascotasByUser(String email) throws NotFoundException{
        if(! userRepository.existsById(email)) throw new NotFoundException("El Usuario no existe en el sistema");

        List<Mascota> mascotas = mascotaRepository.findAll();
        List<Mascota> resultado = new ArrayList<Mascota>();

        for(Mascota mascota : mascotas)
            if(mascota.getUserEmail().equals(email)) resultado.add(mascota);

        return resultado;
    }





    public void deleteMascota(String emailDuenyo, String nombreMascota) throws NotFoundException {
        String id = nombreMascota+emailDuenyo;
        if(! mascotaRepository.existsById(id)) throw new NotFoundException("La Mascota no existe en el sistema");
        mascotaRepository.deleteById(id);
    }

    public void removeDataBase() {
        userRepository.deleteAll();
    }



}
